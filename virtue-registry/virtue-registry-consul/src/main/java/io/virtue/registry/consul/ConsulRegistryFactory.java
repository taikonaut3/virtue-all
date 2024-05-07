package io.virtue.registry.consul;

import io.virtue.common.extension.spi.Extension;
import io.virtue.common.url.URL;
import io.virtue.registry.AbstractRegistryFactory;
import io.virtue.registry.RegistryService;

import static io.virtue.common.constant.Components.Registry.CONSUL;

/**
 * Consul RegistryFactory.
 */
@Extension(CONSUL)
public class ConsulRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected RegistryService create(URL url) {
        return new ConsulRegistryService(url);
    }

}
