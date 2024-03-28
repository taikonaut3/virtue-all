package io.virtue.core.manager;

import io.virtue.common.util.StringUtil;
import io.virtue.core.Callee;
import io.virtue.core.Caller;
import io.virtue.core.Virtue;
import io.virtue.core.config.RegistryConfig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RegistryConfig Manager.
 */
public class RegistryConfigManager extends AbstractRuleManager<RegistryConfig> {

    public RegistryConfigManager(Virtue virtue) {
        super(virtue);
    }

    /**
     * Get global configs.
     *
     * @return
     */
    public List<RegistryConfig> globalConfigs() {
        return getManagerMap().values().stream().
                filter(RegistryConfig::global)
                .collect(Collectors.toList());
    }

    /**
     * Register config.
     *
     * @param registryConfig
     */
    public void register(RegistryConfig registryConfig) {
        String name = StringUtil.isBlank(registryConfig.name()) ? registryConfig.type() : registryConfig.name();
        register(name, registryConfig);
    }

    @Override
    protected void doExecuteRules(RegistryConfig config, List<Callee<?>> matchedCallees, List<Caller<?>> matchedCallers) {
        matchedCallers.forEach(item -> item.addRegistryConfig(config));
    }

}
