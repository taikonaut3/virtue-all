package io.github.astro.virtue.config.manager;

import io.github.astro.virtue.common.util.StringUtil;
import io.github.astro.virtue.config.ClientCaller;
import io.github.astro.virtue.config.ServerCaller;
import io.github.astro.virtue.config.config.RegistryConfig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RegistryConfig Manager
 */
public class RegistryConfigManager extends AbstractRuleManager<RegistryConfig> {

    public RegistryConfigManager(Virtue virtue) {
        super(virtue);
    }

    public List<RegistryConfig> globalConfigs() {
        return getManagerMap().values().stream().
                filter(RegistryConfig::global)
                .collect(Collectors.toList());
    }

    public void register(RegistryConfig registryConfig) {
        String name = StringUtil.isBlank(registryConfig.name()) ? registryConfig.type() : registryConfig.name();
        register(name, registryConfig);
    }

    @Override
    protected void doExecuteRules(RegistryConfig config, List<ServerCaller<?>> matchedServerCallers, List<ClientCaller<?>> matchedClientCallers) {
        matchedClientCallers.forEach(ClientCaller::addRegistryConfig);
    }

}
