package io.virtue.registry.nacos;

import io.virtue.common.spi.ServiceProvider;
import io.virtue.common.url.URL;
import io.virtue.registry.AbstractRegistryFactory;
import io.virtue.registry.Registry;
import io.virtue.common.constant.Components;

/**
 * Use Nacoe-Client
 */
@ServiceProvider(Components.Registry.NACOS)
public class NacosRegistryFactory extends AbstractRegistryFactory {
    @Override
    protected Registry create(URL url) {
        return new NacosRegistry(url);
    }
}
