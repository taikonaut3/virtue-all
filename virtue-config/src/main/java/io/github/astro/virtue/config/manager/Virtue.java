package io.github.astro.virtue.config.manager;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.extension.Accessor;
import io.github.astro.virtue.common.extension.Attribute;
import io.github.astro.virtue.common.extension.AttributeKey;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.spi.ServiceInterface;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.Lifecycle;
import io.github.astro.virtue.config.RemoteCaller;
import io.github.astro.virtue.config.RemoteService;
import io.github.astro.virtue.config.Scheduler;
import io.github.astro.virtue.config.config.ClientConfig;
import io.github.astro.virtue.config.config.RegistryConfig;
import io.github.astro.virtue.config.config.ServerConfig;
import io.github.astro.virtue.config.filter.Filter;
import io.github.astro.virtue.event.EventDispatcher;

import static io.github.astro.virtue.common.constant.Components.DEFAULT;

/**
 * Manage all entry configurations.
 */
@ServiceInterface(DEFAULT)
public interface Virtue extends Accessor, Lifecycle {

    AttributeKey<Virtue> ATTRIBUTE_KEY = AttributeKey.get(Key.VIRTUE);

    /**
     * Virtue name
     *
     * @return name
     */
    String name();

    /**
     * Set name
     *
     * @param name
     * @return current instance
     */
    Virtue name(String name);

    /**
     * Get ConfigManager
     *
     * @return ConfigManager
     */
    ConfigManager configManager();

    /**
     * Get EventDispatcher
     *
     * @return EventDispatcher
     */
    EventDispatcher eventDispatcher();

    /**
     * Get Scheduler
     *
     * @return Scheduler
     */
    Scheduler scheduler();

    /**
     * Get MonitorManager
     *
     * @return MonitorManager
     */
    MonitorManager monitorManager();

    /**
     * Create {@link RemoteCaller} and register into current instance
     *
     * @param target
     * @param <T>
     * @return current instance
     */
    <T> Virtue proxy(Class<T> target);

    /**
     * Create {@link RemoteService} and register into current instance
     *
     * @param target
     * @param <T>
     * @return current instance
     */
    <T> Virtue wrap(T target);

    /**
     * Get RemoteCaller by target Interface
     *
     * @param target
     * @param <T>
     * @return RemoteCaller
     */
    default <T> RemoteCaller<T> remoteCaller(Class<T> target) {
        return configManager().remoteCallerManager().get(target);
    }

    /**
     * Get RemoteService by target class
     *
     * @param target
     * @param <T>
     * @return RemoteService
     */
    default <T> RemoteService<T> remoteService(Class<T> targetClass) {
        return configManager().remoteServiceManager().get(targetClass);
    }

    /**
     * Register RemoteService into current instance
     *
     * @param remoteServices
     * @return current instance
     */
    default Virtue register(RemoteService<?>... remoteServices) {
        for (RemoteService<?> remoteService : remoteServices) {
            configManager().remoteServiceManager().register(remoteService);
        }
        return this;
    }

    /**
     * Register RemoteCaller into current instance
     *
     * @param remoteCallers
     * @return current instance
     */
    default Virtue register(RemoteCaller<?>... remoteCallers) {
        for (RemoteCaller<?> remoteCaller : remoteCallers) {
            configManager().remoteCallerManager().register(remoteCaller);
        }
        return this;
    }

    /**
     * Register ClientConfig into current instance
     *
     * @param configs
     * @return current instance
     */
    default Virtue register(ClientConfig... configs) {
        for (ClientConfig config : configs) {
            configManager().clientConfigManager().register(config);
        }
        return this;
    }

    /**
     * Register ServerConfig into current instance
     *
     * @param configs
     * @return current instance
     */
    default Virtue register(ServerConfig... configs) {
        for (ServerConfig config : configs) {
            configManager().serverConfigManager().register(config);
        }
        return this;
    }

    /**
     * Register RegistryConfig into current instance
     *
     * @param configs
     * @return current instance
     */
    default Virtue register(RegistryConfig... configs) {
        for (RegistryConfig config : configs) {
            configManager().registryConfigManager().register(config);
        }
        return this;
    }

    /**
     * Register Filter into current instance
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
     * Get Application-Name
     *
     * @return Application-Name
     */
    default String applicationName() {
        return configManager().applicationConfig().applicationName();
    }

    /**
     * Set Application-Name
     *
     * @return current instance
     */
    default Virtue applicationName(String applicationName) {
        configManager().applicationConfig().applicationName(applicationName);
        return this;
    }

    /**
     * Get Virtue instance from URL
     *
     * @param url
     * @return Virtue instance
     */
    static Virtue get(URL url) {
        Attribute<Virtue> attribute = url.attribute(ATTRIBUTE_KEY);
        Virtue virtue = attribute.get();
        if (virtue == null) {
            virtue = ExtensionLoader.loadService(Virtue.class, url.getParameter(Key.VIRTUE));
            attribute.set(virtue);
        }
        return virtue;
    }

    /**
     * Get Default Virtue
     *
     * @return Default Virtue instance
     * @see io.github.astro.virtue.rpc.impl.DefaultVirtue
     */
    static Virtue getDefault() {
        return ExtensionLoader.loadService(Virtue.class);
    }
}
