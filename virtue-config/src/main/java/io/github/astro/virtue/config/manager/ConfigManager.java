package io.github.astro.virtue.config.manager;

import io.github.astro.virtue.config.config.ApplicationConfig;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * All Config Manager
 */
@Accessors(fluent = true)
@Getter
public class ConfigManager {

    private final RemoteServiceManager remoteServiceManager;

    private final RemoteCallerManager remoteCallerManager;

    private final ClientConfigManager clientConfigManager;

    private final ServerConfigManager serverConfigManager;

    private final RegistryConfigManager registryConfigManager;

    private final FilterManager filterManager;

    private final ApplicationConfig applicationConfig;

    public ConfigManager(Virtue virtue) {
        remoteServiceManager = new RemoteServiceManager(virtue);
        remoteCallerManager = new RemoteCallerManager(virtue);
        clientConfigManager = new ClientConfigManager(virtue);
        registryConfigManager = new RegistryConfigManager(virtue);
        filterManager = new FilterManager(virtue);
        serverConfigManager = new ServerConfigManager(virtue);
        this.applicationConfig = new ApplicationConfig();
    }

}
