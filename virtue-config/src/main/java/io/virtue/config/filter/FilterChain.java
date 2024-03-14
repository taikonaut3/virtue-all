package io.virtue.config.filter;

import io.virtue.common.spi.ServiceInterface;
import io.virtue.config.Invocation;
import io.virtue.common.constant.Components;

import java.util.List;

/**
 * The filter chain is used to start the filter.
 */
@ServiceInterface(Components.DEFAULT)
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

