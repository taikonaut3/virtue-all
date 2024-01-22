package io.github.astro.virtue.config;

import io.github.astro.virtue.common.extension.AbstractAccessor;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.config.config.*;
import io.github.astro.virtue.config.filter.Filter;
import io.github.astro.virtue.config.manager.ConfigManager;
import io.github.astro.virtue.config.manager.MonitorManager;
import io.github.astro.virtue.config.manager.ProtocolRegistryManager;
import io.github.astro.virtue.event.EventDispatcher;
import io.github.astro.virtue.event.EventDispatcherFactory;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * virtue application config
 * manage each config component
 */
@Accessors(fluent = true)
@Getter
public class Virtue extends AbstractAccessor<Object> implements Lifecycle {

    private static final Virtue RPC_APPLICATION = new Virtue();

    private final AtomicLong connections = new AtomicLong(0);

    private final ConfigManager configManager;

    private final ApplicationConfig appConfig;

    private final MonitorManager monitorManager;

    private final ProtocolRegistryManager protocolRegistryManager;

    private final List<VirtueConfiguration> configurations;

    private EventDispatcher eventDispatcher;

    private Virtue() {
        configManager = new ConfigManager();
        monitorManager = new MonitorManager();
        appConfig = new ApplicationConfig();
        configurations = ExtensionLoader.loadServices(VirtueConfiguration.class);
        protocolRegistryManager = new ProtocolRegistryManager();
        init();
    }

    public static Virtue getDefault() {
        return RPC_APPLICATION;
    }

    public String applicationName() {
        return appConfig.applicationName();
    }

    public void applicationName(String applicationName) {
        appConfig.applicationName(applicationName);
    }

    public SystemInfo newSystemInfo() {
        return new SystemInfo(connections.get());
    }

    public void registerRemoteService(RemoteService<?>... remoteServices) {
        for (RemoteService<?> remoteService : remoteServices) {
            configManager.remoteServiceManager().register(remoteService);
        }
    }

    public void registerRemoteCaller(RemoteCaller<?>... remoteCallers) {
        for (RemoteCaller<?> remoteCaller : remoteCallers) {
            configManager.remoteCallerManager().register(remoteCaller);
        }
    }

    public void registerClientConfig(ClientConfig... configs) {
        for (ClientConfig config : configs) {
            configManager.clientConfigManager().register(config);
        }
    }

    public void registerServerConfig(ServerConfig... configs) {
        for (ServerConfig config : configs) {
            configManager.serverConfigManager().register(config);
        }
    }

    public void registerRegistryConfig(RegistryConfig... configs) {
        for (RegistryConfig config : configs) {
            configManager.registryConfigManager().register(config.name(), config);
        }
    }

    public void registerFilter(String name, Filter filter) {
        configManager.filterManager().register(name, filter);
    }

    @Override
    public void init() {
        for (VirtueConfiguration configuration : configurations) {
            configuration.initBefore(this);
        }
        EventDispatcherConfig eventDispatcherConfig = appConfig.eventDispatcherConfig();
        EventDispatcherFactory eventDispatcherFactory = ExtensionLoader.loadService(EventDispatcherFactory.class, eventDispatcherConfig.type());
        eventDispatcher = eventDispatcherFactory.create(eventDispatcherConfig.toUrl());
        for (VirtueConfiguration configuration : configurations) {
            configuration.initAfter(this);
        }
    }

    public synchronized void start() {
        for (VirtueConfiguration configuration : configurations) {
            configuration.startBefore(this);
        }

        for (RemoteService<?> remoteService : configManager().remoteServiceManager().getRemoteService()) {
            remoteService.start();
        }

        for (RemoteCaller<?> remoteCaller : configManager().remoteCallerManager().getRemoteCallers()) {
            remoteCaller.start();
        }

        for (VirtueConfiguration configuration : configurations) {
            configuration.startAfter(this);
        }
    }

    @Override
    public void stop() {
        for (VirtueConfiguration configuration : configurations) {
            configuration.stopBefore(this);
        }

        for (VirtueConfiguration configuration : configurations) {
            configuration.stopAfter(this);
        }
    }

}
