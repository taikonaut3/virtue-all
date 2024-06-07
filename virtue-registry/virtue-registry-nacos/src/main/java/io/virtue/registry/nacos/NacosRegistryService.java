package io.virtue.registry.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.virtue.common.constant.Key;
import io.virtue.common.exception.RpcException;
import io.virtue.common.url.URL;
import io.virtue.common.util.StringUtil;
import io.virtue.registry.AbstractRegistryService;
import io.virtue.registry.support.RegisterTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * RegistryService based on nacos-client.
 * <a href = "https://github.com/alibaba/nacos">nacos</a>
 */
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
    public void connect(URL url) {
        try {
            namingService = NamingFactory.createNamingService(url.address());
            String serviceName = serviceName(url);
            if (!StringUtil.isBlank(serviceName)) {
                // nacos <project.name>
                System.setProperty("project.name", serviceName);
            }
        } catch (NacosException e) {
            logger.error("Connect to nacos: {} failed", url.address());
            throw RpcException.unwrap(e);
        }
    }

    @Override
    public BiConsumer<RegisterTask, Map<String, String>> createRegisterTask(URL url) {
        String serviceName = serviceName(url);
        return (registerTask, metaData) -> {
            Instance instance = new Instance();
            instance.setInstanceId(instanceId(url));
            instance.setIp(url.host());
            instance.setPort(url.port());
            instance.setMetadata(metaData);
            try {
                namingService.registerInstance(serviceName, instance);
            } catch (NacosException e) {
                logger.error("RegisterInstance is failed from nacos: service:{}-{}", serviceName, url.address());
                throw RpcException.unwrap(e);
            }
        };
    }

    @Override
    public void deregister(URL url) {
        String serviceName = serviceName(url);
        try {
            namingService.deregisterInstance(serviceName, url.host(), url.port());
        } catch (NacosException e) {
            logger.error("DeregisterInstance is failed from nacos: service:{}-{}", serviceName, url.address());
            throw RpcException.unwrap(e);
        }
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
                    urls.add(instanceToUrl(instance));
                }
            }
        } catch (NacosException e) {
            logger.error("SelectInstances is failed from nacos for url:{}", url);
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
                            .map(instance -> instanceToUrl(instance).toString())
                            .toList();
                    discoverHealthServices.put(serviceName, healthServerUrls);
                }
            });
        } catch (NacosException e) {
            logger.error("Subscribe is failed from nacos for service:{}", serviceName);
            throw RpcException.unwrap(e);
        }
    }

    @Override
    public void close() {
        super.close();
        try {
            namingService.shutDown();
        } catch (NacosException e) {
            throw RpcException.unwrap(e);
        }
    }

    private URL instanceToUrl(Instance instance) {
        String protocol = instance.getMetadata().get(Key.PROTOCOL).toLowerCase();
        URL url = new URL(protocol, instance.getIp(), instance.getPort());
        url.addParams(instance.getMetadata());
        return url;
    }
}
