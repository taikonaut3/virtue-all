package io.virtue.rpc;

import io.virtue.common.constant.Key;
import io.virtue.common.extension.spi.Extension;
import io.virtue.common.extension.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.core.Virtue;
import io.virtue.core.VirtueConfiguration;
import io.virtue.core.config.RegistryConfig;
import io.virtue.core.manager.ServerConfigManager;
import io.virtue.event.EventDispatcher;
import io.virtue.event.disruptor.DisruptorEventDispatcher;
import io.virtue.metrics.CalleeMetrics;
import io.virtue.metrics.CallerMetrics;
import io.virtue.metrics.event.CalleeMetricsEvent;
import io.virtue.metrics.event.CalleeMetricsEventListener;
import io.virtue.metrics.event.CallerMetricsEvent;
import io.virtue.metrics.event.CallerMetricsEventListener;
import io.virtue.metrics.filter.CalleeMetricsFilter;
import io.virtue.metrics.filter.CallerMetricsFilter;
import io.virtue.registry.RegistryFactory;
import io.virtue.registry.RegistryService;
import io.virtue.registry.support.RegisterServiceEvent;
import io.virtue.registry.support.RegisterServiceEventListener;
import io.virtue.rpc.event.*;
import io.virtue.rpc.event.listener.*;
import io.virtue.rpc.protocol.AbstractProtocol;
import io.virtue.rpc.protocol.Protocol;
import io.virtue.transport.server.Server;
import io.virtue.transport.supprot.IdleEvent;
import io.virtue.transport.supprot.IdleEventListener;
import io.virtue.transport.supprot.RefreshHeartBeatCountEvent;
import io.virtue.transport.supprot.RefreshHeartBeatCountEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.virtue.common.util.StringUtil.simpleClassName;

/**
 * Registry Global Default Listeners and Open Server.
 */
@Extension("bootstrapConfiguration")
public class BootStrapConfiguration implements VirtueConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(BootStrapConfiguration.class);

    @Override
    public void initBefore(Virtue virtue) {
        ExtensionLoader.addListener(EventDispatcher.class, extension -> {
            if (extension instanceof DisruptorEventDispatcher eventDispatcher) {
                URL url = virtue.configManager().applicationConfig().eventDispatcherConfig().toUrl();
                eventDispatcher.url(url);
            }
        });
        ExtensionLoader.addListener(Protocol.class, extension -> {
            if (extension instanceof AbstractProtocol<?, ?> abstractProtocol) {
                abstractProtocol.virtue(virtue);
            }
        });
    }

    @Override
    public void initAfter(Virtue virtue) {
        // register default event listeners
        EventDispatcher eventDispatcher = virtue.eventDispatcher();
        eventDispatcher.addListener(RequestEvent.class, new RequestEventListener());
        eventDispatcher.addListener(ResponseEvent.class, new ResponseEventListener());
        eventDispatcher.addListener(ClientHandlerExceptionEvent.class, new ClientHandlerExceptionListener());
        eventDispatcher.addListener(ServerHandlerExceptionEvent.class, new ServerHandlerExceptionListener());
        eventDispatcher.addListener(RefreshHeartBeatCountEvent.class, new RefreshHeartBeatCountEventListener());
        eventDispatcher.addListener(IdleEvent.class, new IdleEventListener());
        eventDispatcher.addListener(SendMessageEvent.class, new SendMessageEventListener());
        eventDispatcher.addListener(RegisterServiceEvent.class, new RegisterServiceEventListener());
        eventDispatcher.addListener(CallerMetricsEvent.class, new CallerMetricsEventListener());
        eventDispatcher.addListener(CalleeMetricsEvent.class, new CalleeMetricsEventListener());

        // register default filter
        virtue.register(CalleeMetrics.ATTRIBUTE_KEY.name().toString(), new CalleeMetricsFilter(virtue));
        virtue.register(CallerMetrics.ATTRIBUTE_KEY.name().toString(), new CallerMetricsFilter(virtue));
    }

    @Override
    public void startAfter(Virtue virtue) {
        ServerConfigManager serverConfigManager = virtue.configManager().serverConfigManager();
        List<RegistryConfig> registryConfigs = virtue.configManager().registryConfigManager().globalConfigs();
        List<URL> serverUrls = serverConfigManager.neededOpenServer();
        for (URL serverUrl : serverUrls) {
            if (registryConfigs.isEmpty() && logger.isWarnEnabled()) {
                logger.warn("No globally available RegistryConfig(s)");
            } else {
                for (RegistryConfig registryConfig : registryConfigs) {
                    URL registryConfigUrl = registryConfig.toUrl();
                    registryConfigUrl.set(Virtue.LOCAL_VIRTUE, virtue);
                    registryConfigUrl.addParam(Key.LOCAL_VIRTUE, virtue.name());
                    RegistryFactory registryFactory = ExtensionLoader.loadExtension(RegistryFactory.class, registryConfigUrl.protocol());
                    RegistryService registryService = registryFactory.get(registryConfigUrl);
                    registryService.register(serverUrl);
                }
            }
            // Open Server
            var protocol = ExtensionLoader.loadExtension(Protocol.class, serverUrl.protocol());
            Server server = protocol.openServer(serverUrl);
            logger.info("Opened <{}>{},bind port(s) {}", serverUrl.protocol(), simpleClassName(server), server.port());
        }
    }
}
