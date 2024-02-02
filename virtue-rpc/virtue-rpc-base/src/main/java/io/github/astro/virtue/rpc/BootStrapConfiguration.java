package io.github.astro.virtue.rpc;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.VirtueConfiguration;
import io.github.astro.virtue.config.config.RegistryConfig;
import io.github.astro.virtue.config.manager.ServerConfigManager;
import io.github.astro.virtue.config.manager.Virtue;
import io.github.astro.virtue.event.EventDispatcher;
import io.github.astro.virtue.registry.Registry;
import io.github.astro.virtue.registry.RegistryFactory;
import io.github.astro.virtue.rpc.event.*;
import io.github.astro.virtue.rpc.listener.*;
import io.github.astro.virtue.rpc.protocol.Protocol;
import io.github.astro.virtue.transport.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Registry Global Default Listeners
 */
@ServiceProvider("bootstrapConfiguration")
public class BootStrapConfiguration implements VirtueConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(BootStrapConfiguration.class);

    @Override
    public void initAfter(Virtue virtue) {
        EventDispatcher eventDispatcher = virtue.eventDispatcher();
        eventDispatcher.addListener(HeartBeatEvent.class, new HeartBeatEventListener());
        eventDispatcher.addListener(RequestEvent.class, new RequestEventListener());
        eventDispatcher.addListener(ResponseEvent.class, new ResponseEventListener());
        eventDispatcher.addListener(ClientHandlerExceptionEvent.class, new ClientHandlerExceptionListener());
        eventDispatcher.addListener(ServerHandlerExceptionEvent.class, new ServerHandlerExceptionListener());
    }

    @Override
    public void startAfter(Virtue virtue) {
        ServerConfigManager serverConfigManager = virtue.configManager().serverConfigManager();
        List<RegistryConfig> registryConfigs = virtue.configManager().registryConfigManager().globalConfigs();
        List<URL> serverUrls = serverConfigManager.neededOpenServer();
        for (URL serverUrl : serverUrls) {
            if (registryConfigs.isEmpty()) {
                logger.warn("No globally available RegistryConfig(s)");
            } else {
                for (RegistryConfig registryConfig : registryConfigs) {
                    URL registryConfigUrl = registryConfig.toUrl();
                    registryConfigUrl.attribute(Virtue.ATTRIBUTE_KEY).set(virtue);
                    registryConfigUrl.addParameter(Key.VIRTUE, virtue.name());
                    RegistryFactory registryFactory = ExtensionLoader.loadService(RegistryFactory.class, registryConfigUrl.protocol());
                    Registry registry = registryFactory.get(registryConfigUrl);
                    registry.register(serverUrl);
                }
            }
            // 开启协议端口
            Protocol<?, ?> protocol = ExtensionLoader.loadService(Protocol.class, serverUrl.protocol());
            Server server = protocol.openServer(serverUrl);
        }
    }
}
