package io.virtue.core.config;

import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.url.Parameter;
import io.virtue.common.url.URL;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Registry Config.
 */
@Getter
@Setter
@Accessors(fluent = true, chain = true)
public class RegistryConfig extends UrlTypeConfig {

    @Parameter(Key.GLOBAL)
    private boolean global = true;

    @Parameter(Key.USERNAME)
    private String username;

    @Parameter(Key.PASSWORD)
    private String password;

    @Parameter(Key.ENABLE_HEALTH_CHECK)
    private boolean enableHealthCheck = true;

    @Parameter(Key.CONNECT_TIMEOUT)
    private int connectTimeout = Constant.DEFAULT_CONNECT_TIMEOUT;

    @Parameter(Key.SESSION_TIMEOUT)
    private int sessionTimeout = Constant.DEFAULT_SESSION_TIMEOUT;

    @Parameter(Key.RETRIES)
    private int retries = Constant.DEFAULT_RETIRES;

    @Parameter(Key.RETRY_INTERVAL)
    private int retryInterval = Constant.DEFAULT_INTERVAL;

    @Parameter(Key.HEALTH_CHECK_INTERVAL)
    private int healthCheckInterval = Constant.DEFAULT_HEALTH_CHECK_INTERVAL;

    public RegistryConfig() {

    }

    public RegistryConfig(String type, String address) {
        type(type);
        address(address);
    }

    public RegistryConfig(String authority) {
        URL url = URL.valueOf(authority);
        type(url.protocol());
        address(url.address());
    }

    @Override
    public URL toUrl() {
        return new URL(type, host, port, parameterization());
    }
}
