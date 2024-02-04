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
     * Execute filter chain.
     *
     * @param invocation
     * @param filters    The list of filters to be call
     * @return invocation.invoke()
     */
    Object filter(Invocation invocation, List<Filter> filters);

}

