package io.virtue.registry.consul;

import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.exception.ConnectException;
import io.virtue.common.extension.Attribute;
import io.virtue.common.extension.AttributeKey;
import io.virtue.common.url.URL;
import io.virtue.common.util.StringUtil;
import io.virtue.core.Virtue;
import io.virtue.registry.AbstractRegistryService;
import io.vertx.core.Vertx;
import io.vertx.ext.consul.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsulRegistryService extends AbstractRegistryService {

    private static final Logger logger = LoggerFactory.getLogger(ConsulRegistryService.class);

    private ConsulClient consulClient;

    private Vertx vertx;

    protected ConsulRegistryService(URL url) {
        super(url);
    }

    @Override
    public boolean isActive() {
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
    public void connect(URL url) throws ConnectException {
        try {
            ConsulClientOptions options = new ConsulClientOptions()
                    .setHost(url.host())
                    .setPort(url.port())
                    .setTimeout(url.getIntParam(Key.CONNECT_TIMEOUT));
            Attribute<Vertx> vertxAttribute = Virtue.get(url).attribute(AttributeKey.get(Key.VERTX));
            vertx = vertxAttribute.get();
            if (vertx == null) {
                vertx = Vertx.vertx();
                vertxAttribute.set(vertx);
            }
            consulClient = ConsulClient.create(vertx, options);
        } catch (Exception e) {
            logger.error("Connect to Consul: {} Fail", url.address());
            throw new ConnectException(e);
        }
    }

    @Override
    public void register(URL url) {
        String serviceName = serviceName(url);
        consulClient.deregisterService(serviceName);
        Virtue.get(url).scheduler().addPeriodic(() -> {
            ServiceOptions opts = new ServiceOptions()
                    .setName(serviceName)
                    .setId(instanceId(url))
                    .setAddress(url.host())
                    .setPort(url.port())
                    .setMeta(metaInfo(url));
            if (enableHealthCheck) {
                int healthCheckInterval = url.getIntParam(Key.HEALTH_CHECK_INTERVAL,
                        Constant.DEFAULT_HEALTH_CHECK_INTERVAL);
                CheckOptions checkOpts = new CheckOptions()
                        .setTcp(url.address())
                        .setId(instanceId(url))
                        .setDeregisterAfter((healthCheckInterval * 10) + "ms")
                        .setInterval(healthCheckInterval + "ms");
                opts.setCheckOptions(checkOpts);
            }
            consulClient.registerService(opts, res -> {
                if (res.succeeded()) {
                    logger.trace("Register {}: {} success", serviceName, url.authority());
                } else {
                    logger.error("Register " + serviceName + ": " + url.authority() + " failed", res.cause());
                }
            });
        }, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    protected List<URL> doDiscover(URL url) {
        String serviceName = serviceName(url);
        ArrayList<URL> urls = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        // 获取所有健康检查的节点的URL
        consulClient.healthServiceNodes(serviceName, true).onComplete(res -> {
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
                logger.error("Found " + serviceName + " fail", res.cause());
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
        String serviceName = serviceName(url);
        Watch.service(serviceName, vertx).setHandler(res -> {
            if (res.succeeded()) {
                List<ServiceEntry> serviceEntries = res.nextResult().getList();
                List<String> healthServerUrls = serviceEntries.stream()
                        .filter(instance -> instance.aggregatedStatus() == CheckStatus.PASSING && instance.getService().getMeta().containsKey(Key.PROTOCOL))
                        .map(instance -> serviceEntryToUrl(instance.getService().getMeta().get(Key.PROTOCOL), instance).toString())
                        .toList();
                discoverHealthServices.put(serviceName, healthServerUrls);
            }
        }).start();
    }

    @Override
    public void close() {
        consulClient.close();
    }

    private URL serviceEntryToUrl(String protocol, ServiceEntry entry) {
        Service service = entry.getService();
        Map<String, String> meta = service.getMeta();
        URL serverUrl = new URL(protocol, service.getAddress(), service.getPort());
        serverUrl.addParams(meta);
        return serverUrl;
    }
}
