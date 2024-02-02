package io.github.astro.virtue.config.manager;

import io.github.astro.virtue.common.util.StringUtil;
import io.github.astro.virtue.config.config.ClientConfig;

public class ClientConfigManager extends AbstractManager<ClientConfig> {

    public ClientConfigManager(Virtue virtue) {
        super(virtue);
    }

    public void register(ClientConfig clientConfig) {
        String name = StringUtil.isBlank(clientConfig.name()) ? clientConfig.type() : clientConfig.name();
        register(name, clientConfig);
    }

}
