package io.virtue.rpc.impl;

import io.virtue.common.spi.ServiceProvider;
import io.virtue.config.Invocation;
import io.virtue.config.filter.Filter;
import io.virtue.config.filter.FilterChain;

import java.util.List;

import static io.virtue.common.constant.Components.DEFAULT;

@ServiceProvider(DEFAULT)
public class DefaultFilterChain implements FilterChain {

    @Override
    public Object filter(Invocation invocation, List<Filter> filters) {
        Invocation filterInvocation = Invocation.create(
                invocation.url(),
                invocation.callArgs(),
                invocation::invoke
        );
        return doFilter(invocation, filterInvocation, filters, 0);
    }

    private Object doFilter(Invocation invocation, Invocation filterInvocation, List<Filter> filters, int index) {
        if (filters == null || index == filters.size()) {
            return invocation.invoke();
        }
        Filter filter = filters.get(index);
        return filter.doFilter(filterInvocation.revise(() -> doFilter(invocation, filterInvocation, filters, index + 1)));
    }
}