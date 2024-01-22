package io.github.astro.virtue.registry.zookeeper;

import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.registry.AbstractRegistryFactory;
import io.github.astro.virtue.registry.Registry;

import static io.github.astro.virtue.common.constant.Components.Registry.ZOOKEEPER;

@ServiceProvider(ZOOKEEPER)
public class ZookeeperRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected Registry create(URL url) {
        return null;
        // return new ZookeeperRegistry(url);
    }

}
