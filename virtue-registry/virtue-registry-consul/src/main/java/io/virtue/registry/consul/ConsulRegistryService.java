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
import java.util.function.BiConsumer;

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
            logger.error("Connect to consul: {} failed", url.address());
            throw RpcException.unwrap(e);
        }
    }

    @Override
    public BiConsumer<RegisterTask, Map<String, String>> createRegisterTask(URL url) {
        String serviceName = serviceName(url);
        String serviceId = instanceId(url);
        return (registerTask, metaData) -> {
            ServiceOptions opts = new ServiceOptions()
                    .setName(serviceName)
                    .setId(serviceId)
                    .setAddress(url.host())
                    .setPort(url.port())
                    .setMeta(metaData);
            if (enableHealthCheck) {
                int healthCheckInterval = url.getIntParam(Key.HEALTH_CHECK_INTERVAL, Constant.DEFAULT_HEALTH_CHECK_INTERVAL);
                CheckOptions checkOpts = new CheckOptions()
                        .setTcp(url.address())
                        .setId(serviceId)
                        .setDeregisterAfter((healthCheckInterval * 10) + "ms")
                        .setInterval(healthCheckInterval + "ms");
                opts.setCheckOptions(checkOpts);
            }
            consulClient.registerService(opts, res -> {
                if (res.succeeded() && registerTask.isFirstRun()) {
                    logger.info("Registered {}: {}", serviceName, serviceId);
                    registerTask.isFirstRun(false);
                } else if (!res.succeeded()) {
                    logger.error("Register " + serviceName + ": " + serviceId + " failed", res.cause());
                }
            });
        };
    }

    @Override
    public void deregister(URL url) {
        String serviceId = instanceId(url);
        CountDownLatch latch = new CountDownLatch(1);
        consulClient.deregisterService(serviceId, res -> {
            if (res.succeeded()) {
                logger.info("Deregistered {}: {}", serviceName(url), serviceId);
            } else {
                logger.error("Deregister " + serviceName(url) + ": " + serviceId + " failed", res.cause());
            }
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("", e);
        }
    }

    @Override
    protected List<URL> doDiscover(URL url) {
        String serviceName = serviceName(url);
        ArrayList<URL> urls = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        // Get the urls of all nodes for health checks
        consulClient.healthServiceNodes(serviceName, true).onComplete(res -> {
            if (res.succeeded()) {
                List<ServiceEntry> serviceEntries = res.result().getList();
                if (logger.isDebugEnabled()) {
                    logger.debug("{} found {} services from <{}>", url.uri(), serviceEntries.size(), serviceName);
                }
                if (serviceEntries.isEmpty()) {
                    latch.countDown();
                } else {
                    for (ServiceEntry entry : serviceEntries) {
                        Service service = entry.getService();
                        String protocol = service.getMeta().get(Key.PROTOCOL);
                        if (!StringUtil.isBlank(protocol) && protocol.equalsIgnoreCase(url.protocol())) {
                            urls.add(serviceEntryToUrl(entry));
                        }
                    }
                    latch.countDown();
                }
            } else {
                logger.error("Found " + serviceName + " failed", res.cause());
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
                        .map(instance -> serviceEntryToUrl(instance).toString())
                        .toList();
                discoverHealthServices.put(serviceName, healthServerUrls);
            }
        }).start();
        logger.info("Subscribe service['{}']", serviceName);
    }

    @Override
    public void close() {
        super.close();
        consulClient.close();
    }

    private URL serviceEntryToUrl(ServiceEntry entry) {
        Service service = entry.getService();
        Map<String, String> meta = service.getMeta();
        String protocol = meta.get(Key.PROTOCOL).toLowerCase();
        URL serverUrl = new URL(protocol, service.getAddress(), service.getPort());
        serverUrl.addParams(meta);
        return serverUrl;
    }
}
