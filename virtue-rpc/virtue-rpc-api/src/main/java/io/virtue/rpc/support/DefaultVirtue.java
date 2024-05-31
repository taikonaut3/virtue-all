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
import io.virtue.core.manager.Environment;
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

    private final String name;

    private final Environment environment;

    private final List<VirtueConfiguration> configurations;

    private ConfigManager configManager;

    private MonitorManager monitorManager;

    private Scheduler scheduler;

    private EventDispatcher eventDispatcher;

    private volatile State state;

    public DefaultVirtue() {
        name = DEFAULT;
        environment = new Environment();
        configurations = ExtensionLoader.loadExtensions(VirtueConfiguration.class);
        init();
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
        if (state == null) {
            RpcContext.currentContext().set(LOCAL_VIRTUE, this);
            for (VirtueConfiguration configuration : configurations) {
                configuration.initBefore(this);
            }
            initApplicationComponents();
            for (VirtueConfiguration configuration : configurations) {
                configuration.initAfter(this);
            }
            state = State.INITIALIZED;
        } else {
            throw new IllegalStateException("init() can only be invoked when constructing");
        }
    }

    private void initApplicationComponents() {
        configManager = new ConfigManager(this);
        monitorManager = new MonitorManager();
        scheduler = ExtensionLoader.loadExtension(Scheduler.class);
        ApplicationConfig applicationConfig = configManager.applicationConfig();
        EventDispatcherConfig eventDispatcherConfig = applicationConfig.eventDispatcherConfig();
        eventDispatcher = ExtensionLoader.loadExtension(EventDispatcher.class, eventDispatcherConfig.type());
        Router router = ExtensionLoader.loadExtension(Router.class, applicationConfig.router());
        set(Router.ATTRIBUTE_KEY, router);
    }

    @Override
    public synchronized void start() {
        if (state == State.INITIALIZED || state == State.STOPPED) {
            RpcContext.currentContext().set(LOCAL_VIRTUE, this);
            for (VirtueConfiguration configuration : configurations) {
                configuration.startBefore(this);
            }
            configManager.start();
            for (VirtueConfiguration configuration : configurations) {
                configuration.startAfter(this);
            }
            Runtime.getRuntime().addShutdownHook(new VirtueShutdownHook());
            state = State.STARTED;
            logger.info("Virtue started v{}", Platform.virtueVersion());
        } else {
            throw new IllegalStateException("start() can only be invoked when State is (INITIALIZED|STOPPED)");
        }
    }

    @Override
    public synchronized void stop() {
        if (state == State.STARTED) {
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
            state = State.STOPPED;
            logger.info("Virtue stopped");
        } else {
            throw new IllegalStateException("start() can only be invoked when State is STARTED");
        }
    }

    enum State {
        INITIALIZED, STARTED, STOPPED
    }

    class VirtueShutdownHook extends Thread {

        VirtueShutdownHook() {
            setName(simpleClassName(this));
        }

        @Override
        public void run() {
            Platform.jvmShuttingDown();
            DefaultVirtue.this.stop();
        }
    }
}
