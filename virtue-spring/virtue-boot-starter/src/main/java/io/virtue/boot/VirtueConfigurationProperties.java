package io.virtue.boot;

import io.virtue.core.config.ClientConfig;
import io.virtue.core.config.RegistryConfig;
import io.virtue.core.config.ServerConfig;
import io.virtue.core.manager.Virtue;
import jakarta.annotation.Resource;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "virtue")
public class VirtueConfigurationProperties {

    @Resource
    private Virtue virtue;

    public String getApplicationName() {
        return virtue.applicationName();
    }

    public void setApplicationName(String name) {
        virtue.applicationName(name);
    }

    public List<ServerConfig> getServerConfigs() {
        return virtue.configManager().serverConfigManager().getManagerMap().values().stream().toList();
    }

    public void setServerConfigs(List<ServerConfig> serverConfigs) {
        serverConfigs.forEach(serverConfig -> virtue.configManager().serverConfigManager().register(serverConfig));
    }

    public List<ClientConfig> getClientConfigs() {
        return virtue.configManager().clientConfigManager().getManagerMap().values().stream().toList();
    }

    public void setClientConfigs(List<ClientConfig> clientConfigs) {
        clientConfigs.forEach(clientConfig -> virtue.configManager().clientConfigManager().register(clientConfig));
    }

    public List<RegistryConfig> getRegistryConfigs() {
        return virtue.configManager().registryConfigManager().getManagerMap().values().stream().toList();
    }

    public void setRegistryConfigs(List<RegistryConfig> registryConfigs) {
        registryConfigs.forEach(registryConfig -> virtue.configManager().registryConfigManager().register(registryConfig));
    }

}
