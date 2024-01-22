package io.github.astro.virtue.config;

import io.github.astro.virtue.common.url.Parameterization;
import io.github.astro.virtue.config.config.RegistryConfig;
import io.github.astro.virtue.config.filter.Filter;
import io.github.astro.virtue.config.filter.FilterChain;

import java.util.List;

/**
 * Client/server common Config.
 *
 * @see io.github.astro.virtue.config.annotation.Config
 */
public interface CommonConfig extends Parameterization {

    /**
     * Gets the group name from the @Config.
     */
    String group();

    /**
     * Gets the version number from the @Config.
     */
    String version();

    /**
     * Gets the serialization type from the @Config.
     */
    String serialize();

    /**
     * Gets a list of registry configurations from the @Config.
     */
    List<RegistryConfig> registryConfigs();

    /**
     * Gets a list of filters from the @Config.
     */
    List<Filter> filters();

    /**
     * Gets the filter chain from the @Config.
     */
    FilterChain filterChain();

}

