package io.virtue.core;

import io.virtue.common.url.Parameterization;
import io.virtue.core.filter.Filter;
import io.virtue.core.filter.FilterChain;
import io.virtue.core.annotation.Config;

import java.util.List;

/**
 * Client/server common Config.
 *
 * @see Config
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
     * Add filter.
     *
     * @param filters
     */
    void addFilter(Filter... filters);

    /**
     * Gets a list of filters from the @Config.
     */
    List<Filter> filters();

    /**
     * Gets the filter chain from the @Config.
     */
    FilterChain filterChain();

}

