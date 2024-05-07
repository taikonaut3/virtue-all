package io.virtue.governance.discovery;

import io.virtue.common.exception.RpcException;
import io.virtue.common.extension.spi.Extension;
import io.virtue.common.extension.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.common.util.CollectionUtil;
import io.virtue.core.Invocation;
import io.virtue.registry.RegistryFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.virtue.common.constant.Components.DEFAULT;

/**
 * Default ServiceDiscovery.
 * <p>Deduplication based on address.</p>
 */
@Extension(DEFAULT)
public class DefaultServiceDiscovery implements ServiceDiscovery {

    @Override
    public List<URL> discover(Invocation invocation, URL... registryConfigs) {
        List<URL> availableServiceUrls = new ArrayList<>();
        URL url = invocation.url();
        for (URL registryUrl : registryConfigs) {
            var registryService = ExtensionLoader.loadExtension(RegistryFactory.class, registryUrl.protocol()).get(registryUrl);
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
            throw new RpcException(String.format("Not found available service!,Protocol:%s, Path:%s", url.protocol(), url.path()));
        }
        return availableServiceUrls;
    }
}
