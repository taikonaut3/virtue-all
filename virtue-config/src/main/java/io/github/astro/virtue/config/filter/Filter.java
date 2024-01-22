package io.github.astro.virtue.config.filter;

import io.github.astro.virtue.config.Invocation;

/**
 * Filter that can be applied to an invocation.
 */
public interface Filter {

    /**
     * Executes the filter logic on the given invocation.
     *
     * @param invocation The invocation to be filtered.
     * @return The result of the filter execution.
     */
    Object doFilter(Invocation invocation);

    /**
     * Returns the scope of the filter.
     * By default, it returns FilterScope.PRE.
     *
     * @return The scope of the filter.
     */
    default FilterScope scope() {
        return FilterScope.PRE;
    }

}

