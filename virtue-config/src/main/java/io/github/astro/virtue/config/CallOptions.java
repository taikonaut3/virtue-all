package io.github.astro.virtue.config;

import io.github.astro.virtue.config.annotation.Options;
import io.github.astro.virtue.config.config.RegistryConfig;

import java.util.List;

/**
 * Mapping {@link io.github.astro.virtue.config.annotation.Options}
 */
public interface CallOptions extends CommonConfig {

    /**
     * Supports async call,The default is false.
     * If true,Then the returnType should be {@link java.util.concurrent.CompletableFuture}
     */
    boolean async();

    /**
     * The default is false,Unit: ms.
     *
     * @return The timeout value
     */
    int timeout();

    /**
     * Used {@link io.github.astro.virtue.governance.directory.Directory}.
     * Default is "default" {@link io.github.astro.virtue.governance.directory.DefaultDirectory}
     */
    String directory();

    /**
     * Used {@link io.github.astro.virtue.governance.router.Router}.
     * Default is "weight" {@link io.github.astro.virtue.governance.router.WeightRouter}
     */
    String router();

    /**
     * Used {@link io.github.astro.virtue.governance.loadbalance.LoadBalance}.
     * Default is "random" {@link io.github.astro.virtue.governance.loadbalance.RandomLoadBalance}.
     */
    String loadBalance();

    /**
     * Used {@link io.github.astro.virtue.governance.faulttolerance.FaultTolerance}.
     * Default is "failFast" {@link io.github.astro.virtue.governance.faulttolerance.FailFast}.
     */
    String faultTolerance();

    /**
     * If {@link Options#faultTolerance()} is "failRetry",
     * Number of retries called when an exception occurred.
     */
    int retires();

    /**
     * Gets the direct URL for making the remote service call.
     *
     * @return the direct url
     */
    String directUrl();

    /**
     * Determines whether to reuse the client configuration from the current protocol.
     */
    boolean multiplex();

    /**
     * Gets the client config for the call.
     *
     * @return clientConfig instance
     */
    String clientConfig();

    /**
     * Gets a list of registry configurations from the {@link Options#registries()}.
     */
    List<RegistryConfig> registryConfigs();

    /**
     * Is it only when the first call is made that the registration center is actually connected to Get the available services.
     * The default gets available services when {@link io.github.astro.virtue.config.ClientCaller} creation is complete.
     */
    boolean lazyDiscover();

}

