package io.github.astro.virtue.config.manager;

import io.github.astro.virtue.config.config.ConfigScope;
import io.github.astro.virtue.config.config.RegistryConfig;

import java.util.List;
import java.util.stream.Collectors;

public class RegistryConfigManager extends AbstractManager<RegistryConfig> {

    public List<RegistryConfig> getApplicationScopeConfigs() {
        return getManagerMap().values().stream().
                filter(protocolConfig -> protocolConfig.scope() == ConfigScope.APPLICATION)
                .collect(Collectors.toList());
    }

    public void register(RegistryConfig registryConfig) {
        register(registryConfig.name(), registryConfig);
    }

}
