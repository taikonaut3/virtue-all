package io.virtue.core.manager;

import io.virtue.common.util.StringUtil;
import io.virtue.core.Virtue;
import io.virtue.core.config.ClientConfig;

/**
 * ClientConfig Manager
 */
public class ClientConfigManager extends AbstractManager<ClientConfig> {

    public ClientConfigManager(Virtue virtue) {
        super(virtue);
    }

    public void register(ClientConfig clientConfig) {
        String name = StringUtil.isBlank(clientConfig.name()) ? clientConfig.type() : clientConfig.name();
        register(name, clientConfig);
    }

}
