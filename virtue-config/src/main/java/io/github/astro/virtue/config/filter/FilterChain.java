package io.github.astro.virtue.config.filter;

import io.github.astro.virtue.common.spi.ServiceInterface;
import io.github.astro.virtue.config.Invocation;

import java.util.List;

import static io.github.astro.virtue.common.constant.Components.DEFAULT;

/**
 * The filter chain is used to start the filter.
 */
@ServiceInterface(DEFAULT)
public interface FilterChain {

    /**
     * Applies the list of filters to the given invocation.
     *
     * @param invocation The invocation to be filtered.
     * @param filters    The list of filters to be applied.
     * @return The result of the filtered invocation.
     */
    Object filter(Invocation invocation, List<Filter> filters);

}

