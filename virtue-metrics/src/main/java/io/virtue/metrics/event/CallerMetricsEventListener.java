package io.virtue.metrics.event;

import io.virtue.common.util.AtomicUtil;
import io.virtue.event.EventListener;
import io.virtue.metrics.CallerMetrics;

import java.util.concurrent.atomic.LongAdder;

/**
 * CalleeMetricsEvent Listener.
 */
public class CallerMetricsEventListener implements EventListener<CallerMetricsEvent> {

    @Override
    public void onEvent(CallerMetricsEvent event) {
        CallerMetrics callerMetrics = event.source();
        callerMetrics.callCount().increment();
        if (event.hasException()) {
            callerMetrics.failureCallCount().increment();
        } else {
            callerMetrics.successCallCount().increment();
            long currentDuration = event.currentDuration();
            AtomicUtil.updateAtomicLong(callerMetrics.maxCallTime(), old -> Math.max(old, currentDuration));
            AtomicUtil.updateAtomicLong(callerMetrics.minCallTime(), old -> old == 0 ? currentDuration : Math.min(old, currentDuration));
            AtomicUtil.updateAtomicReference(callerMetrics.averageCallTime(), old -> {
                LongAdder successCallCount = callerMetrics.successCallCount();
                double totalSuccessTime = (successCallCount.doubleValue() - 1) * old;
                return (totalSuccessTime + currentDuration) / successCallCount.doubleValue();
            });
        }
    }
}
