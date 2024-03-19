package io.virtue.core.manager;

import io.virtue.core.ClientCaller;
import io.virtue.core.ServerCaller;
import io.virtue.core.filter.Filter;

import java.util.List;

/**
 * Filter Manager
 */
public class FilterManager extends AbstractRuleManager<Filter> {

    public FilterManager(Virtue virtue) {
        super(virtue);
    }

    @Override
    protected void doExecuteRules(Filter filter, List<ServerCaller<?>> matchedServerCallers, List<ClientCaller<?>> matchedClientCallers) {
        matchedServerCallers.forEach(item->item.addFilter(filter));
        matchedClientCallers.forEach(item->item.addFilter(filter));
    }

}
