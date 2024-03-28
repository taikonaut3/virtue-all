package io.virtue.rpc.support;

import io.virtue.common.spi.ServiceProvider;
import io.virtue.core.Invocation;
import io.virtue.core.filter.Filter;
import io.virtue.core.filter.FilterChain;
import io.virtue.core.support.TransferableInvocation;

import java.util.List;

import static io.virtue.common.constant.Components.DEFAULT;

/**
 * Default FilterChain impl.
 */
@ServiceProvider(DEFAULT)
public class DefaultFilterChain implements FilterChain {

    @Override
    public Object filter(Invocation invocation, List<Filter> filters) {
        FilterInvocation filterInvocation = new FilterInvocation(invocation);
        return doFilter(invocation, filterInvocation, filters, 0);
    }

    private Object doFilter(Invocation invocation, Invocation filterInvocation, List<Filter> filters, int index) {
        if (filters == null || index == filters.size()) {
            return invocation.invoke();
        }
        Filter filter = filters.get(index);
        return filter.doFilter(filterInvocation.revise(() -> doFilter(invocation, filterInvocation, filters, index + 1)));
    }

    static class FilterInvocation extends TransferableInvocation {
        FilterInvocation(Invocation invocation) {
            basic(invocation.invoker(), invocation.args());
            url(invocation.url());
        }
    }
}