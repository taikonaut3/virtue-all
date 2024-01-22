package io.github.astro.rpc.virtue.config;

import io.github.astro.virtue.config.config.ServerConfig;

import static io.github.astro.virtue.common.constant.Components.Protocol.VIRTUE;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/6 14:00
 */
public class VirtueServerConfig extends ServerConfig {
    public VirtueServerConfig() {
        super(VIRTUE);
        port(2663);
    }
}
