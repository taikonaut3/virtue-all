package io.github.astro.virtue.config.filter;

import io.github.astro.virtue.config.Invocation;

/**
 * Filter can interceptor rpc call
 */
public interface Filter {

    /**
     * Execute the filter logic.
     *
     * @param invocation
     * @return Should always use {@link Invocation#invoke()} to return
     */
    Object doFilter(Invocation invocation);

    /**
     * Returns the scope of the filter.
     * By default,
     * For the client, {@link FilterScope#PRE} means it is called before obtaining the actual address,
     * and {@link FilterScope#POST} is only supported by the client and is called after obtaining the actual address;
     * For the server, {@link FilterScope#PRE} means it is called before invoking the target method.
     *
     * @return The scope of the filter.
     */
    default FilterScope scope() {
        return FilterScope.PRE;
    }

}

