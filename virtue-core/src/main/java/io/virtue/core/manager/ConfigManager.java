package io.virtue.core.manager;

import io.virtue.core.Virtue;
import io.virtue.core.config.ApplicationConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * All Config Manager.
 */
@Getter
@Accessors(fluent = true)
public class ConfigManager {

    private final RemoteServiceManager remoteServiceManager;

    private final RemoteCallerManager remoteCallerManager;

    private final ClientConfigManager clientConfigManager;

    private final ServerConfigManager serverConfigManager;

    private final RegistryConfigManager registryConfigManager;

    private final RouterConfigManager routerConfigManager;

    private final FilterManager filterManager;

    @Setter
    private ApplicationConfig applicationConfig;

    public ConfigManager(Virtue virtue) {
        remoteServiceManager = new RemoteServiceManager(virtue);
        remoteCallerManager = new RemoteCallerManager(virtue);
        clientConfigManager = new ClientConfigManager(virtue);
        registryConfigManager = new RegistryConfigManager(virtue);
        routerConfigManager = new RouterConfigManager(virtue);
        filterManager = new FilterManager(virtue);
        serverConfigManager = new ServerConfigManager(virtue);
        this.applicationConfig = new ApplicationConfig();
    }

}
