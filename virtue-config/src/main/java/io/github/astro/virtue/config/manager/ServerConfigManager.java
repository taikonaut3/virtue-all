package io.github.astro.virtue.config.manager;

import io.github.astro.virtue.config.config.ServerConfig;

/**
 * Server Config Manager
 */
public class ServerConfigManager extends AbstractManager<ServerConfig> {

    /**
     * 注册一个ServerConfig,协议类型是唯一的
     *
     * @param config
     */
    public void register(ServerConfig config) {
        register(config.type(), config);
    }
}
