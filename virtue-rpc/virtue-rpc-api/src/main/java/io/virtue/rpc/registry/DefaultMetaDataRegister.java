package io.virtue.rpc.registry;

import io.virtue.common.constant.Key;
import io.virtue.common.extension.spi.Extension;
import io.virtue.common.extension.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.core.SystemInfo;
import io.virtue.core.Virtue;
import io.virtue.core.config.ApplicationConfig;
import io.virtue.registry.RegisterMetaData;
import io.virtue.rpc.protocol.Protocol;

import java.util.Map;

import static io.virtue.common.constant.Components.DEFAULT;

/**
 * Register default meta data to registry.
 */
@Extension(DEFAULT)
public class DefaultMetaDataRegister implements RegisterMetaData {

    @Override
    public void process(URL url, Map<String, String> metaData) {
        Virtue virtue = Virtue.ofLocal(url);
        ApplicationConfig applicationConfig = virtue.configManager().applicationConfig();
        SystemInfo systemInfo = new SystemInfo(virtue);
        Protocol protocol = ExtensionLoader.loadExtension(Protocol.class, url.protocol());
        int connections = protocol.endpoints().servers().stream().mapToInt(server -> server.channels().length).sum();
        systemInfo.connections(connections);
        metaData.putAll(systemInfo.toMap());
        metaData.put(Key.PROTOCOL, url.protocol());
        metaData.put(Key.WEIGHT, String.valueOf(applicationConfig.weight()));
        metaData.put(Key.GROUP, applicationConfig.group());
    }
}
