package io.github.astro.virtue.registry.nacos;

import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.registry.AbstractRegistryFactory;
import io.github.astro.virtue.registry.Registry;

import static io.github.astro.virtue.common.constant.Components.Registry.NACOS;

/**
 * Use Nacoe-Client
 */
@ServiceProvider(NACOS)
public class NacosRegistryFactory extends AbstractRegistryFactory {
    @Override
    protected Registry create(URL url) {
        return new NacosRegistry(url);
    }
}
