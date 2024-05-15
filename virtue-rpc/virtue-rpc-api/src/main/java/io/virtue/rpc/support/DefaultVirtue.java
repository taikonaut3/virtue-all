package io.virtue.rpc.support;

import io.virtue.common.constant.Platform;
import io.virtue.common.executor.RpcThreadPool;
import io.virtue.common.extension.AbstractAccessor;
import io.virtue.common.extension.RpcContext;
import io.virtue.common.extension.spi.Extension;
import io.virtue.common.extension.spi.ExtensionLoader;
import io.virtue.core.Scheduler;
import io.virtue.core.Virtue;
import io.virtue.core.VirtueConfiguration;
import io.virtue.core.config.ApplicationConfig;
import io.virtue.core.config.EventDispatcherConfig;
import io.virtue.core.manager.ConfigManager;
import io.virtue.core.manager.MonitorManager;
import io.virtue.event.EventDispatcher;
import io.virtue.governance.router.Router;
import io.virtue.transport.RpcFuture;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.virtue.common.constant.Components.DEFAULT;
import static io.virtue.common.util.StringUtil.simpleClassName;

/**
 * Virtue application core manage each core component.
 */
@Getter
@Accessors(fluent = true)
@Extension(DEFAULT)
public class DefaultVirtue extends AbstractAccessor implements Virtue {

    private static final Logger logger = LoggerFactory.getLogger(DefaultVirtue.class);
    private final ConfigManager configManager;
    private final MonitorManager monitorManager;
    private final List<VirtueConfiguration> configurations;
    private final Scheduler scheduler;
    private final String name;
    private EventDispatcher eventDispatcher;
    private volatile boolean started = false;

    public DefaultVirtue() {
        name = DEFAULT;
        configManager = new ConfigManager(this);
        monitorManager = new MonitorManager();
        configurations = ExtensionLoader.loadExtensions(VirtueConfiguration.class);
        scheduler = ExtensionLoader.loadExtension(Scheduler.class);
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
        RpcContext.currentContext().set(LOCAL_VIRTUE, this);
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
        eventDispatcher = ExtensionLoader.loadExtension(EventDispatcher.class, eventDispatcherConfig.type());
        Router router = ExtensionLoader.loadExtension(Router.class, applicationConfig.router());
        set(Router.ATTRIBUTE_KEY, router);
    }

    @Override
    public synchronized void start() {
        if (!started) {
            RpcContext.currentContext().set(LOCAL_VIRTUE, this);
            for (VirtueConfiguration configuration : configurations) {
                configuration.startBefore(this);
            }
            configManager.start();
            for (VirtueConfiguration configuration : configurations) {
                configuration.startAfter(this);
            }
            started = true;
            logger.info("Virtue started v{}", Platform.virtueVersion());
            Runtime.getRuntime().addShutdownHook(new VirtueShutdownHook());
        }
    }

    @Override
    public synchronized void stop() {
        if (started) {
            RpcContext.currentContext().set(LOCAL_VIRTUE, this);
            for (VirtueConfiguration configuration : configurations) {
                configuration.stopBefore(this);
            }
            configManager.stop();
            RpcThreadPool.clear();
            RpcFuture.clear();
            ExtensionLoader.clearLoader();
            for (VirtueConfiguration configuration : configurations) {
                configuration.stopAfter(this);
            }
            eventDispatcher.close();
            started = false;
            logger.info("Virtue stopped");
        }
    }

    class VirtueShutdownHook extends Thread {

        public VirtueShutdownHook() {
            setName(simpleClassName(this));
        }

        @Override
        public void run() {
            Platform.jvmShuttingDown();
            DefaultVirtue.this.stop();
        }
    }
}
