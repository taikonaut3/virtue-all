package io.virtue.governance.discovery;

import io.virtue.common.exception.RpcException;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.spi.ServiceProvider;
import io.virtue.common.url.URL;
import io.virtue.common.util.CollectionUtil;
import io.virtue.core.Invocation;
import io.virtue.registry.RegistryService;
import io.virtue.registry.RegistryFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.virtue.common.constant.Components.DEFAULT;

/**
 * must open virtue-registry
 */
@ServiceProvider(DEFAULT)
public class DefaultServiceDiscovery implements ServiceDiscovery {

    @Override
    public List<URL> discover(Invocation invocation, URL... registryConfigs) {
        ArrayList<URL> availableServiceUrls = new ArrayList<>();
        URL url = invocation.url();
        for (URL registryUrl : registryConfigs) {
            RegistryFactory registryFactory = ExtensionLoader.loadService(RegistryFactory.class, registryUrl.protocol());
            RegistryService registryService = registryFactory.get(registryUrl);
            List<URL> discoverUrls = registryService.discover(url);
            if (CollectionUtil.isNotEmpty(discoverUrls)) {
                for (URL discoverUrl : discoverUrls) {
                    CollectionUtil.addToList(
                            availableServiceUrls,
                            (existUrl, newUrl) -> Objects.equals(existUrl.address(), newUrl.address()),
                            discoverUrl);
                }
            }
        }
        if (availableServiceUrls.isEmpty()) {
            throw new RpcException("Not found available service!,Path:" + url.path());
        }
        return availableServiceUrls;
    }
}
