package io.virtue.core;

import io.virtue.common.constant.Key;
import io.virtue.common.extension.Accessor;
import io.virtue.common.extension.AttributeKey;
import io.virtue.common.extension.RpcContext;
import io.virtue.common.extension.spi.Extensible;
import io.virtue.common.extension.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.core.config.ApplicationConfig;
import io.virtue.core.config.ClientConfig;
import io.virtue.core.config.RegistryConfig;
import io.virtue.core.config.ServerConfig;
import io.virtue.core.filter.Filter;
import io.virtue.core.manager.ConfigManager;
import io.virtue.core.manager.Environment;
import io.virtue.core.manager.MonitorManager;
import io.virtue.event.EventDispatcher;
import org.intellij.lang.annotations.Language;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static io.virtue.common.constant.Components.DEFAULT;

/**
 * Manage all entry configurations.
 */
@Extensible(DEFAULT)
public interface Virtue extends Accessor, Lifecycle {
    AttributeKey<Virtue> LOCAL_VIRTUE = AttributeKey.of(Key.LOCAL_VIRTUE);

    AttributeKey<Virtue> CLIENT_VIRTUE = AttributeKey.of(Key.CLIENT_VIRTUE);

    AttributeKey<Virtue> SERVER_VIRTUE = AttributeKey.of(Key.SERVER_VIRTUE);

    /**
     * Get local virtue instance.
     *
     * @param url
     * @return
     */
    static Virtue ofLocal(URL url) {
        return getVirtue(LOCAL_VIRTUE, url);
    }

    /**
     * Get client virtue instance.
     *
     * @param url
     * @return
     */
    static Virtue ofClient(URL url) {
        return getVirtue(CLIENT_VIRTUE, url);
    }

    /**
     * Get server virtue instance.
     *
     * @param url
     * @return
     */
    static Virtue ofServer(URL url) {
        return getVirtue(SERVER_VIRTUE, url);
    }

    private static Virtue getVirtue(AttributeKey<Virtue> key, URL url) {
        Virtue virtue = url.get(key);
        if (virtue == null) {
            virtue = RpcContext.currentContext().get(key);
            if (virtue == null) {
                virtue = ExtensionLoader.loadExtension(Virtue.class, url.getParam(key.name().toString(), DEFAULT));
            }
            if (virtue != null) {
                url.set(key, virtue);
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
        return ExtensionLoader.loadExtension(Virtue.class);
    }

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
     * Get environment.
     *
     * @return
     */
    Environment environment();

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
}
