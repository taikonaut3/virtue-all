package io.virtue.core.manager;

import io.virtue.common.util.StringUtil;
import io.virtue.core.Virtue;
import io.virtue.core.config.ClientConfig;

/**
 * ClientConfig Manager.
 */
public class ClientConfigManager extends AbstractManager<ClientConfig> {

    public ClientConfigManager(Virtue virtue) {
        super(virtue);
    }

    /**
     * Add client config.
     *
     * @param clientConfig
     */
    public void register(ClientConfig clientConfig) {
        String name = StringUtil.isBlankOrDefault(clientConfig.name(), clientConfig.type());
        register(name, clientConfig);
    }

}
