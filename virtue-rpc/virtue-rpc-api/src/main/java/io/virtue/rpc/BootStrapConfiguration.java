package io.virtue.rpc;

import io.virtue.common.constant.Key;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.spi.ServiceProvider;
import io.virtue.common.url.URL;
import io.virtue.core.VirtueConfiguration;
import io.virtue.core.config.RegistryConfig;
import io.virtue.core.manager.ServerConfigManager;
import io.virtue.core.manager.Virtue;
import io.virtue.event.EventDispatcher;
import io.virtue.registry.RegistryService;
import io.virtue.registry.RegistryFactory;
import io.virtue.rpc.event.*;
import io.virtue.rpc.listener.*;
import io.virtue.rpc.protocol.Protocol;
import io.virtue.transport.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Registry Global Default Listeners and Open Server
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
                    RegistryService registryService = registryFactory.get(registryConfigUrl);
                    registryService.register(serverUrl);
                }
            }
            // Open Server
            Protocol<?, ?> protocol = ExtensionLoader.loadService(Protocol.class, serverUrl.protocol());
            Server server = protocol.openServer(serverUrl);
            logger.info("Opened Server[{}] for Protocol[{}] and bind Port(s) {}", server.getClass().getSimpleName(), protocol.protocol(), server.port());
        }
    }
}
