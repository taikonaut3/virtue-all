package io.github.astro.virtue.config.manager;

import io.github.astro.virtue.config.config.ClientConfig;
import io.github.astro.virtue.config.config.ConfigScope;

import java.util.List;
import java.util.stream.Collectors;

public class ClientConfigManager extends AbstractManager<ClientConfig> {

    public List<ClientConfig> getApplicationScopeConfigs() {
        return getManagerMap().values().stream().
                filter(clientConfig -> clientConfig.scope() == ConfigScope.APPLICATION)
                .collect(Collectors.toList());
    }

    public void register(ClientConfig clientConfig) {
        register(clientConfig.name(), clientConfig);
    }

}
