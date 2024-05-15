package io.virtue.core.manager;

import io.virtue.core.Lifecycle;
import io.virtue.core.RemoteCaller;
import io.virtue.core.RemoteService;
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
public class ConfigManager implements Lifecycle {

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

    @Override
    public void start() {
        registryConfigManager.executeRules();
        filterManager.executeRules();
        for (RemoteService<?> remoteService : remoteServiceManager.remoteServices()) {
            remoteService.start();
        }
        for (RemoteCaller<?> remoteCaller : remoteCallerManager.remoteCallers()) {
            remoteCaller.start();
        }
    }

    @Override
    public void stop() {
        remoteServiceManager.clear();
        remoteCallerManager.clear();
        clientConfigManager.clear();
        remoteCallerManager.clear();
        routerConfigManager.clear();
        filterManager.clear();
        serverConfigManager.clear();
    }

}
