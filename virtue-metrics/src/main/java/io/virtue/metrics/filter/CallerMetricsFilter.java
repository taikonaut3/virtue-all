package io.virtue.metrics.filter;

import io.virtue.common.exception.RpcException;
import io.virtue.core.Invocation;
import io.virtue.core.Invoker;
import io.virtue.core.MatchScope;
import io.virtue.core.Virtue;
import io.virtue.core.filter.Filter;
import io.virtue.core.manager.FilterManager;
import io.virtue.metrics.CallerMetrics;
import io.virtue.metrics.event.CallerMetricsEvent;

/**
 * Callee Metrics Filter.
 */
public class CallerMetricsFilter implements Filter {

    public CallerMetricsFilter(Virtue virtue) {
        FilterManager manager = virtue.configManager().filterManager();
        manager.addProtocolRule(this, MatchScope.CALLER, ".*");
    }

    @Override
    public Object doFilter(Invocation invocation) {
        Invoker<?> invoker = invocation.invoker();
        CallerMetrics callerMetrics = invoker.get(CallerMetrics.ATTRIBUTE_KEY);
        boolean hasException = false;
        long start = System.nanoTime();
        try {
            return invocation.invoke();
        } catch (RpcException e) {
            hasException = true;
            throw e;
        } finally {
            long currentDuration = 0;
            if (!hasException) {
                currentDuration = (System.nanoTime() - start) / 1_000_000;
            }
            invoker.virtue().eventDispatcher().dispatch(new CallerMetricsEvent(callerMetrics, hasException, currentDuration));
        }
    }
}
