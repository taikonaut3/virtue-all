package io.github.astro.virtue.config.config;

import io.github.astro.virtue.common.constant.Constant;
import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.url.Parameter;
import io.github.astro.virtue.common.url.URL;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Getter
@Setter
public class RegistryConfig extends UrlTypeConfig {

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

    @Override
    public URL toUrl() {
        return new URL(type, host, port, parameterization());
    }

}
