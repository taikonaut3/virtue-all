package io.github.astro.virtue.config.manager;

import io.github.astro.virtue.common.util.StringUtil;
import io.github.astro.virtue.config.config.RegistryConfig;

import java.util.List;
import java.util.stream.Collectors;

public class RegistryConfigManager extends AbstractManager<RegistryConfig> {

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

}
