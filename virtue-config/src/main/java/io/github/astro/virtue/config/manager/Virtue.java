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

@ServiceInterface(DEFAULT)
public interface Virtue extends Accessor, Lifecycle {

    AttributeKey<Virtue> ATTRIBUTE_KEY = AttributeKey.get(Key.VIRTUE);

    static Virtue get(URL url) {
        Attribute<Virtue> attribute = url.attribute(ATTRIBUTE_KEY);
        Virtue virtue = attribute.get();
        if (virtue == null) {
            virtue = ExtensionLoader.loadService(Virtue.class, url.getParameter(Key.VIRTUE));
            attribute.set(virtue);
        }
        return virtue;
    }

    static Virtue getDefault() {
        return ExtensionLoader.loadService(Virtue.class);
    }

    String name();

    Virtue name(String name);

    ConfigManager configManager();

    EventDispatcher eventDispatcher();

    Scheduler scheduler();

    MonitorManager monitorManager();

    <T> Virtue proxy(Class<T> target);

    <T> Virtue wrap(T target);

    default <T> RemoteCaller<T> remoteCaller(Class<T> target) {
        return configManager().remoteCallerManager().get(target);
    }

    default <T> RemoteService<T> remoteService(Class<T> targetClass) {
        return configManager().remoteServiceManager().get(targetClass);
    }

    default Virtue register(RemoteService<?>... remoteServices) {
        for (RemoteService<?> remoteService : remoteServices) {
            configManager().remoteServiceManager().register(remoteService);
        }
        return this;
    }

    default Virtue register(RemoteCaller<?>... remoteCallers) {
        for (RemoteCaller<?> remoteCaller : remoteCallers) {
            configManager().remoteCallerManager().register(remoteCaller);
        }
        return this;
    }

    default Virtue register(ClientConfig... configs) {
        for (ClientConfig config : configs) {
            configManager().clientConfigManager().register(config);
        }
        return this;
    }

    default Virtue register(ServerConfig... configs) {
        for (ServerConfig config : configs) {
            configManager().serverConfigManager().register(config);
        }
        return this;
    }

    default Virtue register(RegistryConfig... configs) {
        for (RegistryConfig config : configs) {
            configManager().registryConfigManager().register(config);
        }
        return this;
    }

    default Virtue register(String name, Filter filter) {
        configManager().filterManager().register(name, filter);
        return this;
    }

    default String applicationName() {
        return configManager().applicationConfig().applicationName();
    }

    default Virtue applicationName(String applicationName) {
        configManager().applicationConfig().applicationName(applicationName);
        return this;
    }
}
