package io.github.astro.virtue.config.manager;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
public class ConfigManager {

    private final RemoteServiceManager remoteServiceManager;

    private final RemoteCallerManager remoteCallerManager;

    private final ClientConfigManager clientConfigManager;

    private final ServerConfigManager serverConfigManager;

    private final RegistryConfigManager registryConfigManager;

    private final FilterManager filterManager;

    public ConfigManager() {
        remoteServiceManager = new RemoteServiceManager();
        remoteCallerManager = new RemoteCallerManager();
        clientConfigManager = new ClientConfigManager();
        registryConfigManager = new RegistryConfigManager();
        filterManager = new FilterManager();
        serverConfigManager = new ServerConfigManager();
    }

}
