package io.virtue.core.manager;

import io.virtue.core.Callee;
import io.virtue.core.Caller;
import io.virtue.core.Virtue;
import io.virtue.core.filter.Filter;

import java.util.List;

/**
 * Filter Manager.
 */
public class FilterManager extends AbstractRuleManager<Filter> {

    public FilterManager(Virtue virtue) {
        super(virtue);
    }

    @Override
    protected void doExecuteRules(Filter filter, List<Callee<?>> matchedCallees, List<Caller<?>> matchedCallers) {
        matchedCallees.forEach(item -> item.addFilter(filter));
        matchedCallers.forEach(item -> item.addFilter(filter));
    }

}
