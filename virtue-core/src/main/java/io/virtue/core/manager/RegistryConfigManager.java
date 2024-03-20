package io.virtue.core.manager;

import io.virtue.common.util.StringUtil;
import io.virtue.core.ClientCaller;
import io.virtue.core.ServerCaller;
import io.virtue.core.config.RegistryConfig;

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
        matchedClientCallers.forEach(item -> item.addRegistryConfig(config));
    }

}