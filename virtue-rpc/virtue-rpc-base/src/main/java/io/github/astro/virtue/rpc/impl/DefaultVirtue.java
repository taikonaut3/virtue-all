package io.github.astro.virtue.rpc.impl;

import io.github.astro.virtue.common.extension.AbstractAccessor;
import io.github.astro.virtue.common.extension.RpcContext;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.config.RemoteCaller;
import io.github.astro.virtue.config.RemoteService;
import io.github.astro.virtue.config.Scheduler;
import io.github.astro.virtue.config.VirtueConfiguration;
import io.github.astro.virtue.config.config.EventDispatcherConfig;
import io.github.astro.virtue.config.manager.ConfigManager;
import io.github.astro.virtue.config.manager.MonitorManager;
import io.github.astro.virtue.config.manager.Virtue;
import io.github.astro.virtue.event.EventDispatcher;
import io.github.astro.virtue.event.EventDispatcherFactory;
import io.github.astro.virtue.rpc.ComplexRemoteCaller;
import io.github.astro.virtue.rpc.ComplexRemoteService;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

import static io.github.astro.virtue.common.constant.Components.DEFAULT;

/**
 * virtue application config
 * manage each config component
 */
@Accessors(fluent = true)
@Getter
@ServiceProvider(DEFAULT)
public class DefaultVirtue extends AbstractAccessor implements Virtue {

    private final ConfigManager configManager;
    private final MonitorManager monitorManager;
    private final List<VirtueConfiguration> configurations;
    private final Scheduler scheduler;
    private String name;
    private EventDispatcher eventDispatcher;

    public DefaultVirtue() {
        name(DEFAULT);
        configManager = new ConfigManager(this);
        monitorManager = new MonitorManager();
        configurations = ExtensionLoader.loadServices(VirtueConfiguration.class);
        scheduler = ExtensionLoader.loadService(Scheduler.class);
        init();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Virtue name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Scheduler scheduler() {
        return scheduler;
    }

    @Override
    public <T> Virtue proxy(Class<T> interfaceType) {
        ComplexRemoteCaller<T> remoteCaller = new ComplexRemoteCaller<>(this, interfaceType);
        register(remoteCaller);
        return this;
    }

    @Override
    public <T> Virtue wrap(T target) {
        ComplexRemoteService<T> remoteService = new ComplexRemoteService<>(this, target);
        register(remoteService);
        return this;
    }

    @Override
    public void init() {
        RpcContext.getContext().attribute(ATTRIBUTE_KEY).set(this);
        for (VirtueConfiguration configuration : configurations) {
            configuration.initBefore(this);
        }
        EventDispatcherConfig eventDispatcherConfig = configManager.applicationConfig().eventDispatcherConfig();
        EventDispatcherFactory eventDispatcherFactory = ExtensionLoader.loadService(EventDispatcherFactory.class, eventDispatcherConfig.type());
        eventDispatcher = eventDispatcherFactory.get(eventDispatcherConfig.toUrl());
        for (VirtueConfiguration configuration : configurations) {
            configuration.initAfter(this);
        }
    }

    public synchronized void start() {
        RpcContext.getContext().attribute(ATTRIBUTE_KEY).set(this);
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
        RpcContext.getContext().attribute(ATTRIBUTE_KEY).set(this);
        for (VirtueConfiguration configuration : configurations) {
            configuration.stopBefore(this);
        }

        for (VirtueConfiguration configuration : configurations) {
            configuration.stopAfter(this);
        }
    }

}
