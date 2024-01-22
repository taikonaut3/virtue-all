package io.github.astro.rpc.config;

import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.config.Invocation;
import io.github.astro.virtue.config.filter.Filter;
import io.github.astro.virtue.config.filter.FilterChain;

import java.util.List;

@ServiceProvider("default")
public class DefaultFilterChain implements FilterChain {

    @Override
    public Object filter(Invocation invocation, List<Filter> filters) {
        Invocation filterInvocation = Invocation.create(
                invocation.url(),
                invocation.callArgs(),
                Invocation::invoke
        );
        return doFilter(invocation, filterInvocation, filters, 0);
    }

    private Object doFilter(Invocation invocation, Invocation filterInvocation, List<Filter> filters, int index) {
        if (filters == null || index == filters.size()) {
            return invocation.invoke();
        }
        Filter filter = filters.get(index);
        return filter.doFilter(filterInvocation.turnInvoke(inv -> doFilter(invocation, inv, filters, index + 1)));
    }
}