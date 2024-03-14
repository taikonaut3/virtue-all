package io.virtue.registry.zookeeper;

import io.virtue.common.spi.ServiceProvider;
import io.virtue.common.url.URL;
import io.virtue.registry.AbstractRegistryFactory;
import io.virtue.registry.Registry;
import io.virtue.common.constant.Components;

@ServiceProvider(Components.Registry.ZOOKEEPER)
public class ZookeeperRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected Registry create(URL url) {
        return null;
        // return new ZookeeperRegistry(url);
    }

}