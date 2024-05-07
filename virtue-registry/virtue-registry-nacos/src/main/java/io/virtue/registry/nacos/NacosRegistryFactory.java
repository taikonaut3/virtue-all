package io.virtue.registry.nacos;

import io.virtue.common.constant.Components;
import io.virtue.common.extension.spi.Extension;
import io.virtue.common.url.URL;
import io.virtue.registry.AbstractRegistryFactory;
import io.virtue.registry.RegistryService;

/**
 * Nacos RegistryFactory.
 */
@Extension(Components.Registry.NACOS)
public class NacosRegistryFactory extends AbstractRegistryFactory {
    @Override
    protected RegistryService create(URL url) {
        return new NacosRegistryService(url);
    }
}
