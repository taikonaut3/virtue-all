package io.virtue.core;

import io.virtue.common.constant.Components;
import io.virtue.common.constant.Key;
import io.virtue.common.extension.Accessor;
import io.virtue.common.extension.Attribute;
import io.virtue.common.extension.AttributeKey;
import io.virtue.common.extension.RpcContext;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.spi.ServiceInterface;
import io.virtue.common.url.URL;
import io.virtue.core.config.ApplicationConfig;
import io.virtue.core.config.ClientConfig;
import io.virtue.core.config.RegistryConfig;
import io.virtue.core.config.ServerConfig;
import io.virtue.core.filter.Filter;
import io.virtue.core.manager.ConfigManager;
import io.virtue.core.manager.MonitorManager;
import io.virtue.event.EventDispatcher;
import org.intellij.lang.annotations.Language;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Manage all entry configurations.
 */
@ServiceInterface(Components.DEFAULT)
public interface Virtue extends Accessor, Lifecycle {

    AttributeKey<Virtue> ATTRIBUTE_KEY = AttributeKey.get(Key.VIRTUE);

    /**
     * Virtue name.
     *
     * @return name
     */
    String name();

    /**
     * Get configManager.
     *
     * @return configManager
     */
    ConfigManager configManager();

    /**
     * Get eventDispatcher.
     *
     * @return eventDispatcher
     */
    EventDispatcher eventDispatcher();

    /**
     * Get scheduler.
     *
     * @return scheduler
     */
    Scheduler scheduler();

    /**
     * Get monitorManager.
     *
     * @return monitorManager
     */
    MonitorManager monitorManager();

    /**
     * Create {@link RemoteCaller} and register into current instance.
     *
     * @param target
     * @param <T>
     * @return current instance
     */
    <T> Virtue proxy(Class<T> target);

    /**
     * Create {@link RemoteService} and register into current instance.
     *
     * @param target
     * @param <T>
     * @return current instance
     */
    <T> Virtue wrap(T target);

    /**
     * Get remoteCaller by target interface.
     *
     * @param target
     * @param <T>
     * @return remoteCaller
     */
    default <T> RemoteCaller<T> remoteCaller(Class<T> target) {
        return configManager().remoteCallerManager().get(target);
    }

    /**
     * Get remoteService by target class.
     *
     * @param targetClass
     * @param <T>
     * @return remoteService
     */
    default <T> RemoteService<T> remoteService(Class<T> targetClass) {
        return configManager().remoteServiceManager().get(targetClass);
    }

    /**
     * Config Router rule.
     *
     * @param urlRegex
     * @param targetRegex
     * @return current instance
     */
    default Virtue router(@Language("RegExp") String urlRegex, @Language("RegExp") String targetRegex) {
        configManager().routerConfigManager().register(urlRegex, targetRegex);
        return this;
    }

    /**
     * Register remoteService into current instance.
     *
     * @param remoteServices
     * @return current instance
     */
    default Virtue register(RemoteService<?>... remoteServices) {
        Optional.ofNullable(remoteServices)
                .ifPresent(services -> Arrays.stream(services)
                        .filter(Objects::nonNull)
                        .forEach(remoteService -> configManager().remoteServiceManager().register(remoteService)));
        return this;
    }

    /**
     * Register remoteCaller into current instance.
     *
     * @param remoteCallers
     * @return current instance
     */
    default Virtue register(RemoteCaller<?>... remoteCallers) {
        Optional.ofNullable(remoteCallers)
                .ifPresent(callers -> Arrays.stream(callers)
                        .filter(Objects::nonNull)
                        .forEach(remoteCaller -> configManager().remoteCallerManager().register(remoteCaller)));
        return this;
    }

    /**
     * Register clientConfig into current instance.
     *
     * @param clientConfigs
     * @return current instance
     */
    default Virtue register(ClientConfig... clientConfigs) {
        Optional.ofNullable(clientConfigs)
                .ifPresent(configs -> Arrays.stream(configs)
                        .filter(Objects::nonNull)
                        .forEach(config -> configManager().clientConfigManager().register(config)));
        return this;
    }

    /**
     * Register serverConfig into current instance.
     *
     * @param serverConfigs
     * @return current instance
     */
    default Virtue register(ServerConfig... serverConfigs) {
        Optional.ofNullable(serverConfigs)
                .ifPresent(configs -> Arrays.stream(configs)
                        .filter(Objects::nonNull)
                        .forEach(config -> configManager().serverConfigManager().register(config)));
        return this;
    }

    /**
     * Register registryConfig into current instance.
     *
     * @param registryConfigs
     * @return current instance
     */
    default Virtue register(RegistryConfig... registryConfigs) {
        Optional.ofNullable(registryConfigs)
                .ifPresent(configs -> Arrays.stream(configs)
                        .filter(Objects::nonNull)
                        .forEach(config -> configManager().registryConfigManager().register(config)));
        return this;
    }

    /**
     * Register filter into current instance.
     *
     * @param name
     * @param filter
     * @return current instance
     */
    default Virtue register(String name, Filter filter) {
        configManager().filterManager().register(name, filter);
        return this;
    }

    /**
     * Application Config.
     *
     * @param config
     * @return current instance
     */
    default Virtue application(ApplicationConfig config) {
        configManager().applicationConfig(config);
        return this;
    }

    /**
     * Get application-name.
     *
     * @return application-name
     */
    default String applicationName() {
        return configManager().applicationConfig().name();
    }

    /**
     * Set application-name.
     *
     * @param applicationName
     * @return current instance
     */
    default Virtue applicationName(String applicationName) {
        configManager().applicationConfig().name(applicationName);
        return this;
    }

    /**
     * Get virtue instance from url.
     *
     * @param url
     * @return Virtue instance
     */
    static Virtue get(URL url) {
        Attribute<Virtue> attribute = url.attribute(ATTRIBUTE_KEY);
        Virtue virtue = attribute.get();
        if (virtue == null) {
            virtue = RpcContext.currentContext().attribute(ATTRIBUTE_KEY).get();
            if (virtue == null) {
                virtue = ExtensionLoader.loadService(Virtue.class, url.getParam(Key.VIRTUE));
            }
            if (virtue != null) {
                attribute.set(virtue);
            }
        }
        return virtue;
    }

    /**
     * Get default virtue instance.
     *
     * @return default virtue instance
     * @see io.virtue.rpc.support.DefaultVirtue
     */
    static Virtue getDefault() {
        return ExtensionLoader.loadService(Virtue.class);
    }
}
