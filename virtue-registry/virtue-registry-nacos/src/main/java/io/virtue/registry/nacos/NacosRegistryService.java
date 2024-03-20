package io.virtue.registry.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.virtue.common.constant.Key;
import io.virtue.common.exception.ConnectException;
import io.virtue.common.exception.RpcException;
import io.virtue.common.url.URL;
import io.virtue.common.util.StringUtil;
import io.virtue.core.manager.Virtue;
import io.virtue.registry.AbstractRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NacosRegistryService extends AbstractRegistryService {

    private static final Logger logger = LoggerFactory.getLogger(NacosRegistryService.class);

    private NamingService namingService;

    protected NacosRegistryService(URL url) {
        super(url);
    }

    @Override
    public boolean isActive() {
        return namingService.getServerStatus().equals("UP");
    }

    @Override
    public void connect(URL url) throws ConnectException {
        try {
            namingService = NamingFactory.createNamingService(url.address());
            String serviceName = serviceName(url);
            if (!StringUtil.isBlank(serviceName)) {
                System.setProperty("project.name", serviceName);
            }
        } catch (NacosException e) {
            logger.error("Connect to Nacos: {} Fail", url.address());
            throw new ConnectException(e);
        }
    }

    @Override
    public void register(URL url) {
        String serviceName = serviceName(url);
        try {
            namingService.deregisterInstance(serviceName, url.host(), url.port());
        } catch (NacosException e) {
            logger.error("DeregisterInstance is Failed from Nacos: service:{}-{}", serviceName, url.address());
            throw RpcException.unwrap(e);
        }
        Virtue.get(url).scheduler().addPeriodic(() -> {
            Instance instance = new Instance();
            instance.setInstanceId(instanceId(url));
            instance.setIp(url.host());
            instance.setPort(url.port());
            instance.setMetadata(metaInfo(url));
            try {
                namingService.registerInstance(serviceName, instance);
            } catch (NacosException e) {
                logger.error("RegisterInstance is Failed from Nacos: service:{}-{}", serviceName, url.address());
                throw RpcException.unwrap(e);
            }
        }, 0, 5, TimeUnit.SECONDS);

    }

    @Override
    protected List<URL> doDiscover(URL url) {
        String serviceName = serviceName(url);
        ArrayList<URL> urls = new ArrayList<>();
        try {
            List<Instance> instances = namingService.selectInstances(serviceName, true);
            for (Instance instance : instances) {
                String protocol = instance.getMetadata().get(Key.PROTOCOL);
                if (!StringUtil.isBlank(protocol) && protocol.equalsIgnoreCase(url.protocol())) {
                    urls.add(instanceToUrl(protocol, instance));
                }
            }
        } catch (NacosException e) {
            logger.error("SelectInstances is Failed from Nacos for Url:{}", url);
            throw RpcException.unwrap(e);
        }
        return urls;
    }

    @Override
    protected void subscribeService(URL url) {
        String serviceName = serviceName(url);
        try {
            namingService.subscribe(serviceName, event -> {
                if (event instanceof NamingEvent namingEvent) {
                    List<Instance> instances = namingEvent.getInstances();
                    List<String> healthServerUrls = instances.stream()
                            .filter(instance -> instance.isHealthy() && instance.getMetadata().containsKey(Key.PROTOCOL))
                            .map(instance -> instanceToUrl(instance.getMetadata().get(Key.PROTOCOL), instance).toString())
                            .toList();
                    discoverHealthServices.put(serviceName, healthServerUrls);
                }
            });
        } catch (NacosException e) {
            logger.error("Subscribe is Failed from Nacos for Service:{}", serviceName);
            throw RpcException.unwrap(e);
        }
    }

    @Override
    public void close() {
        try {
            namingService.shutDown();
        } catch (NacosException e) {
            throw RpcException.unwrap(e);
        }
    }

    private URL instanceToUrl(String protocol, Instance instance) {
        URL url = new URL(protocol, instance.getIp(), instance.getPort());
        url.addParameters(instance.getMetadata());
        return url;
    }
}
