package io.virtue.registry.consul;

import io.vertx.core.Vertx;
import io.vertx.ext.consul.*;
import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.exception.RpcException;
import io.virtue.common.extension.AttributeKey;
import io.virtue.common.url.URL;
import io.virtue.common.util.StringUtil;
import io.virtue.core.Virtue;
import io.virtue.registry.AbstractRegistryService;
import io.virtue.registry.support.RegisterTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * RegistryService based on vertx-consul.
 * <a href = "https://github.com/vert-x3/vertx-consul-client">vertx-consul-client</a>
 */
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
    public void connect(URL url) {
        try {
            ConsulClientOptions options = new ConsulClientOptions()
                    .setHost(url.host())
                    .setPort(url.port())
                    .setTimeout(url.getIntParam(Key.CONNECT_TIMEOUT, Constant.DEFAULT_CONNECT_TIMEOUT));
            AttributeKey<Vertx> vertxKey = AttributeKey.of(Key.VERTX);
            Virtue virtue = Virtue.ofLocal(url);
            vertx = virtue.get(vertxKey);
            if (vertx == null) {
                vertx = Vertx.vertx();
                virtue.set(vertxKey, vertx);
            }
            consulClient = ConsulClient.create(vertx, options);
        } catch (Exception e) {
            logger.error("Connect to Consul: {} Fail", url.address());
            throw RpcException.unwrap(e);
        }
    }

    @Override
    public RegisterTask doRegister(URL url) {
        String serviceName = serviceName(url);
        Consumer<RegisterTask> task = registerTask -> {
            ServiceOptions opts = new ServiceOptions()
                    .setName(serviceName)
                    .setId(instanceId(url))
                    .setAddress(url.host())
                    .setPort(url.port())
                    .setMeta(metaInfo(url));
            if (enableHealthCheck) {
                int healthCheckInterval = url.getIntParam(Key.HEALTH_CHECK_INTERVAL, Constant.DEFAULT_HEALTH_CHECK_INTERVAL);
                CheckOptions checkOpts = new CheckOptions()
                        .setTcp(url.address())
                        .setId(instanceId(url))
                        .setDeregisterAfter((healthCheckInterval * 10) + "ms")
                        .setInterval(healthCheckInterval + "ms");
                opts.setCheckOptions(checkOpts);
            }
            consulClient.registerService(opts, res -> {
                if (res.succeeded() && !registerTask.isUpdate()) {
                    logger.info("Register {}: {} success", serviceName, url.authority());
                } else if (!res.succeeded()) {
                    logger.error("Register " + serviceName + ": " + url.authority() + " failed", res.cause());
                }
            });
        };
        RegisterTask registerTask = new RegisterTask(task, false);
        registerTask.run();
        return registerTask;
    }

    @Override
    public void deregister(URL url) {
        String serviceId = instanceId(url);
        consulClient.deregisterService(serviceId, res -> {
            if (res.succeeded()) {
                logger.info("Deregister {}: {} success", serviceId, url.authority());
            } else {
                logger.error("Deregister " + serviceId + ": " + url.authority() + " failed", res.cause());
            }
        });
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
                if (logger.isDebugEnabled()) {
                    logger.debug("{} Found {} services from <{}>", url.uri(), serviceEntries.size(), serviceName);
                }
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
                        .filter(instance -> instance.aggregatedStatus() == CheckStatus.PASSING
                                && instance.getService().getMeta().containsKey(Key.PROTOCOL))
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
