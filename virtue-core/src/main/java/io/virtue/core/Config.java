package io.virtue.core;

import io.virtue.common.url.Parameterization;
import io.virtue.core.filter.Filter;
import io.virtue.core.filter.FilterChain;

import java.util.List;

/**
 * Client/server common Config.
 *
 * @see io.virtue.core.annotation.Config
 */
public interface Config extends Parameterization {

    /**
     * The group from the @Config.
     */
    String group();

    /**
     * The version from the @Config.
     */
    String version();

    /**
     * The serialization type from the @Config.
     */
    String serialization();

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

