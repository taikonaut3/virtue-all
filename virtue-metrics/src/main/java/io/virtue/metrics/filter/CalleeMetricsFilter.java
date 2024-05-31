package io.virtue.metrics.filter;

import io.virtue.common.exception.RpcException;
import io.virtue.core.Invocation;
import io.virtue.core.Invoker;
import io.virtue.core.MatchScope;
import io.virtue.core.Virtue;
import io.virtue.core.filter.Filter;
import io.virtue.core.manager.FilterManager;
import io.virtue.metrics.CalleeMetrics;
import io.virtue.metrics.event.CalleeMetricsEvent;

/**
 * Callee Metrics Filter.
 */
public class CalleeMetricsFilter implements Filter {

    public CalleeMetricsFilter(Virtue virtue) {
        FilterManager manager = virtue.configManager().filterManager();
        manager.addProtocolRule(this, MatchScope.CALLEE, ".*");
    }

    @Override
    public Object doFilter(Invocation invocation) {
        Invoker<?> invoker = invocation.invoker();
        CalleeMetrics calleeMetrics = invoker.get(CalleeMetrics.ATTRIBUTE_KEY);
        boolean hasException = false;
        long start = System.currentTimeMillis();
        try {
            return invocation.invoke();
        } catch (RpcException e) {
            hasException = true;
            throw e;
        } finally {
            long currentDuration = 0;
            if (!hasException) {
                long end = System.currentTimeMillis();
                currentDuration = end - start;
            }
            invoker.virtue().eventDispatcher().dispatch(new CalleeMetricsEvent(calleeMetrics, hasException, currentDuration));
        }
    }
}
