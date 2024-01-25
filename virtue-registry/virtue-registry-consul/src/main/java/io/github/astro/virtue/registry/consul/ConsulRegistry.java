package io.github.astro.virtue.registry.consul;

import io.github.astro.virtue.common.constant.Constant;
import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.common.util.StringUtil;
import io.github.astro.virtue.config.Virtue;
import io.github.astro.virtue.registry.AbstractRegistry;
import io.vertx.core.Vertx;
import io.vertx.ext.consul.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsulRegistry extends AbstractRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ConsulRegistry.class);
    private ConsulClient consulClient;
    private Vertx vertx;

    protected ConsulRegistry(URL url) {
        super(url);
    }

    @Override
    public boolean isConnected() {
        AtomicBoolean isConnected = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);
        consulClient.agentInfo().onComplete(ar -> {
            isConnected.set(ar.succeeded());
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("", e);
        }
        return isConnected.get();
    }

    @Override
    public void connect(URL url) {
        ConsulClientOptions options = new ConsulClientOptions()
                .setHost(url.host())
                .setPort(url.port())
                .setTimeout(url.getIntParameter(Key.CONNECT_TIMEOUT));
        vertx = (Vertx) Virtue.getDefault().getDataOrPut("vertx", Vertx.vertx());
        consulClient = ConsulClient.create(vertx, options);
    }

    @Override
    public void register(URL url) {
        String application = url.getParameter(Key.APPLICATION);
        consulClient.deregisterService(application);
        vertx.setPeriodic(0, 5000, ar -> {
            Map<String, String> systemInfo = Virtue.getDefault().newSystemInfo().toMap();
            HashMap<String, String> registryMeta = new HashMap<>(systemInfo);
            registryMeta.put(Key.PROTOCOL, url.protocol());
            ServiceOptions opts = new ServiceOptions()
                    .setName(application)
                    .setId(application + "-" + url.protocol() + ":" + url.port())
                    .setAddress(url.host())
                    .setPort(url.port())
                    .setMeta(registryMeta);
            if (enableHealthCheck) {
                int healthCheckInterval = url.getIntParameter(Key.HEALTH_CHECK_INTERVAL,
                        Constant.DEFAULT_HEALTH_CHECK_INTERVAL);
                CheckOptions checkOpts = new CheckOptions()
                        .setTcp(url.getAddress())
                        .setId(url.authority())
                        .setDeregisterAfter((healthCheckInterval * 10) + "ms")
                        .setInterval(healthCheckInterval + "ms");
                opts.setCheckOptions(checkOpts);
            }
            consulClient.registerService(opts, res -> {
                if (res.succeeded()) {
                    logger.trace("Register {}: {} success", application, url.authority());
                } else {
                    logger.error("Register " + application + ": " + url.authority() + " failed", res.cause());
                }
            });
        });
    }

    @Override
    protected List<URL> doDiscover(URL url) {
        String application = url.getParameter(Key.APPLICATION);
        ArrayList<URL> urls = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        // 获取所有健康检查的节点的URL
        consulClient.healthServiceNodes(application, true).onComplete(res -> {
            if (res.succeeded()) {
                List<ServiceEntry> serviceEntries = res.result().getList();
                logger.debug("Found {} services for URL: {},", url, serviceEntries.size());
                if (serviceEntries.isEmpty()) {
                    latch.countDown();
                } else {
                    for (ServiceEntry entry : serviceEntries) {
                        Service service = entry.getService();
                        String protocol = service.getMeta().get(Key.PROTOCOL);
                        if (!StringUtil.isBlank(protocol) && protocol.equalsIgnoreCase(url.protocol())) {
                            urls.add(serviceEntryToUrl(url.protocol(), entry));
                        }
                    }
                    latch.countDown();
                }
            } else {
                logger.error("Found " + application + " fail", res.cause());
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("", e);
        }
        return urls;
    }

    @Override
    protected void subscribeService(URL url) {
        String application = url.getParameter(Key.APPLICATION);
        Watch.service(application, vertx).setHandler(res -> {
            if (res.succeeded()) {
                List<ServiceEntry> serviceEntries = res.nextResult().getList();
                List<String> healthServerUrl = serviceEntries.stream()
                        .filter(serviceEntry -> serviceEntry.aggregatedStatus() == CheckStatus.PASSING
                                && serviceEntry.getService().getMeta().containsKey(Key.PROTOCOL)).
                        map(entry -> serviceEntryToUrl(entry.getService().getMeta().get(Key.PROTOCOL), entry).toString()).toList();
                discoverHealthServices.put(application, healthServerUrl);
            }
        }).start();
    }

    @Override
    public void destroy() {
        consulClient.close();
    }

    private URL serviceEntryToUrl(String protocol, ServiceEntry entry) {
        Service service = entry.getService();
        Map<String, String> meta = service.getMeta();
        URL serverUrl = new URL(protocol, service.getAddress(), service.getPort());
        serverUrl.addParameters(meta);
        return serverUrl;
    }
}
