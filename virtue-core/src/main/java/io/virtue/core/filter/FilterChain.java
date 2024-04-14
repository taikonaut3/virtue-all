package io.virtue.core.filter;

import io.virtue.common.constant.Components;
import io.virtue.common.spi.Extensible;
import io.virtue.core.Invocation;

import java.util.List;

/**
 * The filter chain is used to start the filter.
 */
@Extensible(Components.DEFAULT)
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

