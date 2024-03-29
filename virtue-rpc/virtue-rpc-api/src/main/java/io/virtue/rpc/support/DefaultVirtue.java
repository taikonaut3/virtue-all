package io.virtue.rpc.support;

import io.virtue.common.extension.AbstractAccessor;
import io.virtue.common.extension.RpcContext;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.spi.ServiceProvider;
import io.virtue.core.RemoteCaller;
import io.virtue.core.RemoteService;
import io.virtue.core.Scheduler;
import io.virtue.core.VirtueConfiguration;
import io.virtue.core.config.ApplicationConfig;
import io.virtue.core.config.EventDispatcherConfig;
import io.virtue.core.manager.ConfigManager;
import io.virtue.core.manager.MonitorManager;
import io.virtue.core.Virtue;
import io.virtue.event.EventDispatcher;
import io.virtue.governance.router.Router;
import io.virtue.transport.Transporter;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

import static io.virtue.common.constant.Components.DEFAULT;

/**
 * Virtue application core manage each core component.
 */
@Getter
@Accessors(fluent = true)
@ServiceProvider(DEFAULT)
public class DefaultVirtue extends AbstractAccessor implements Virtue {

    private final ConfigManager configManager;
    private final MonitorManager monitorManager;
    private final List<VirtueConfiguration> configurations;
    private final Scheduler scheduler;
    private final String name;
    private EventDispatcher eventDispatcher;

    public DefaultVirtue() {
        name = DEFAULT;
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
        RpcContext.currentContext().attribute(ATTRIBUTE_KEY).set(this);
        for (VirtueConfiguration configuration : configurations) {
            configuration.initBefore(this);
        }
        initApplicationComponents();
        for (VirtueConfiguration configuration : configurations) {
            configuration.initAfter(this);
        }
    }

    private void initApplicationComponents() {
        ApplicationConfig applicationConfig = configManager.applicationConfig();
        EventDispatcherConfig eventDispatcherConfig = applicationConfig.eventDispatcherConfig();
        eventDispatcher = ExtensionLoader.load(EventDispatcher.class)
                .conditionOnConstructor(eventDispatcherConfig.toUrl())
                .getService(eventDispatcherConfig.type());
        Router router = ExtensionLoader.loadService(Router.class, applicationConfig.router());
        Transporter transporter = ExtensionLoader.loadService(Transporter.class, applicationConfig.transport());
        attribute(Router.ATTRIBUTE_KEY).set(router);
        attribute(Transporter.ATTRIBUTE_KEY).set(transporter);
    }

    @Override
    public synchronized void start() {
        RpcContext.currentContext().attribute(ATTRIBUTE_KEY).set(this);
        for (VirtueConfiguration configuration : configurations) {
            configuration.startBefore(this);
        }
        configManager.filterManager().executeRules();
        configManager.registryConfigManager().executeRules();
        for (RemoteService<?> remoteService : configManager().remoteServiceManager().remoteServices()) {
            remoteService.start();
        }
        for (RemoteCaller<?> remoteCaller : configManager().remoteCallerManager().remoteCallers()) {
            remoteCaller.start();
        }
        for (VirtueConfiguration configuration : configurations) {
            configuration.startAfter(this);
        }
    }

    @Override
    public void stop() {
        RpcContext.currentContext().attribute(ATTRIBUTE_KEY).set(this);
        for (VirtueConfiguration configuration : configurations) {
            configuration.stopBefore(this);
        }
        for (VirtueConfiguration configuration : configurations) {
            configuration.stopAfter(this);
        }
    }

}
