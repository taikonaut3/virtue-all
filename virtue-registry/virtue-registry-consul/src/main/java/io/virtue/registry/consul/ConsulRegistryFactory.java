package io.virtue.registry.consul;

import io.virtue.common.constant.Components;
import io.virtue.common.spi.ServiceProvider;
import io.virtue.common.url.URL;
import io.virtue.registry.AbstractRegistryFactory;
import io.virtue.registry.RegistryService;

/**
 * Consul RegistryFactory.
 */
@ServiceProvider(Components.Registry.CONSUL)
public class ConsulRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected RegistryService create(URL url) {
        return new ConsulRegistryService(url);
    }

}
