package io.virtue.core;

import io.virtue.core.config.RegistryConfig;

import java.util.List;

/**
 * Mapping {@link io.virtue.core.annotation.Options}.
 */
public interface Options {

    /**
     * The group from the @Config.
     */
    String group();

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
     * Used {@link io.virtue.governance.discovery.ServiceDiscovery}.
     * Default is "default" {@link io.virtue.governance.discovery.DefaultServiceDiscovery}
     */
    String serviceDiscovery();

    /**
     * Used {@link io.virtue.governance.router.Router}.
     * Default is "weight" {@link io.virtue.governance.router.WeightRouter}
     */
    String router();

    /**
     * Used {@link io.virtue.governance.loadbalance.LoadBalance}.
     * Default is "random" {@link io.virtue.governance.loadbalance.RandomLoadBalance}.
     */
    String loadBalance();

    /**
     * Used {@link io.virtue.governance.faulttolerance.FaultTolerance}.
     * Default is "failFast" {@link io.virtue.governance.faulttolerance.FailFast}.
     */
    String faultTolerance();

    /**
     * If {@link io.virtue.core.annotation.Options#faultTolerance()} is "failRetry",
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
     * Gets the client core for the call.
     *
     * @return clientConfig instance
     */
    String clientConfig();

    /**
     * Add RegistryConfigs.
     *
     * @param configs
     */
    void addRegistryConfig(RegistryConfig... configs);

    /**
     * Gets a list of registry configurations from the {@link io.virtue.core.annotation.Options#registries()}.
     */
    List<RegistryConfig> registryConfigs();

}

