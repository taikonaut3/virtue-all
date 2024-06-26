package io.virtue.core.filter;

import io.virtue.common.util.CollectionUtil;

import java.util.List;

/**
 * Filter scope.
 */
public enum FilterScope {

    /**
     * Before selecting the target address.
     */
    PRE,

    /**
     * After selecting the target address.
     */
    POST;

    /**
     * Filters the list of filters based on the current scope.
     *
     * @param filters The list of filters to be filtered.
     * @return The filtered list of filters.
     */
    public List<Filter> filterScope(List<Filter> filters) {
        if (CollectionUtil.isEmpty(filters)) {
            return filters;
        }
        return filters.stream().filter(filter -> filter.scope() == this).toList();
    }
}

