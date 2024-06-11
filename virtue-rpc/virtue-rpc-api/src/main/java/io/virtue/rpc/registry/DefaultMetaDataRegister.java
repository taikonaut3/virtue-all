package io.virtue.rpc.registry;

import io.virtue.common.constant.Key;
import io.virtue.common.extension.spi.Extension;
import io.virtue.common.url.URL;
import io.virtue.core.Virtue;
import io.virtue.core.config.ApplicationConfig;
import io.virtue.registry.MetaDataRegister;

import java.util.Map;

import static io.virtue.common.constant.Components.DEFAULT;

/**
 * Register default meta data to registry.
 */
@Extension(DEFAULT)
public class DefaultMetaDataRegister implements MetaDataRegister {

    @Override
    public void process(URL url, Map<String, String> metaData) {
        Virtue virtue = Virtue.ofLocal(url);
        ApplicationConfig applicationConfig = virtue.configManager().applicationConfig();
        DefaultRegistryMetaData defaultRegistryMetaData = new DefaultRegistryMetaData(url);
        metaData.putAll(defaultRegistryMetaData.toMap());
        metaData.put(Key.PROTOCOL, url.protocol());
        metaData.put(Key.WEIGHT, String.valueOf(applicationConfig.weight()));
        metaData.put(Key.GROUP, applicationConfig.group());
    }
}
