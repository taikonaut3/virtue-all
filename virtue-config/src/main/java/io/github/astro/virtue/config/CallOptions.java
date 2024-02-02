package io.github.astro.virtue.config;

import io.github.astro.virtue.config.config.RegistryConfig;

import java.util.List;

/**
 * Represents the options for making a remote service call.
 */
public interface CallOptions extends CommonConfig {

    /**
     * Checks if the call should be executed asynchronously.
     *
     * @return true if the call should be executed asynchronously, false otherwise.
     */
    boolean async();

    /**
     * Gets the load balancing strategy to be used for the call.
     *
     * @return The load balancing strategy.
     */
    String loadBalance();

    /**
     * Gets the directory where the remote service is located.
     *
     * @return The directory of the remote service.
     */
    String directory();

    /**
     * Gets the router to be used for routing the call.
     *
     * @return The router.
     */
    String router();

    /**
     * Gets the fault tolerance strategy to be used for the call.
     *
     * @return The fault tolerance strategy.
     */
    String faultTolerance();

    /**
     * Gets the timeout value for the call in milliseconds.
     *
     * @return The timeout value.
     */
    int timeout();

    /**
     * Gets the number of retries for the call.
     *
     * @return The number of retries.
     */
    int retires();

    /**
     * Checks if multiplexing should be used for the call.
     *
     * @return true if multiplexing should be used, false otherwise.
     */
    boolean multiplex();

    /**
     * Gets the client configuration for the call.
     *
     * @return The client configuration.
     */
    String clientConfig();

    /**
     * Gets a list of registry configurations from the @Config.
     */
    List<RegistryConfig> registryConfigs();

    /**
     * Is it only when the first call is made that the registration center is actually
     * connected to Get the available services
     */
    boolean lazyDiscover();

}

