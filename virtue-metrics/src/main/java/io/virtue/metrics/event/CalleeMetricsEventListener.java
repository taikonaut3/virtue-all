package io.virtue.metrics.event;

import io.virtue.common.util.AtomicUtil;
import io.virtue.event.EventListener;
import io.virtue.metrics.CalleeMetrics;

import java.util.concurrent.atomic.LongAdder;

/**
 * CalleeMetricsEvent Listener.
 */
public class CalleeMetricsEventListener implements EventListener<CalleeMetricsEvent> {

    @Override
    public void onEvent(CalleeMetricsEvent event) {
        CalleeMetrics calleeMetrics = event.source();
        calleeMetrics.requestCount().increment();
        if (event.hasException()) {
            calleeMetrics.failureCount().increment();
        } else {
            calleeMetrics.successCount().increment();
            long currentDuration = event.currentDuration();
            AtomicUtil.updateAtomicLong(calleeMetrics.maxResponseTime(), old -> Math.max(old, currentDuration));
            AtomicUtil.updateAtomicLong(calleeMetrics.minResponseTime(), old -> old == 0 ? currentDuration : Math.min(old, currentDuration));
            AtomicUtil.updateAtomicReference(calleeMetrics.averageResponseTime(), old -> {
                LongAdder successCount = calleeMetrics.successCount();
                double totalSuccessTime = (successCount.doubleValue() - 1) * old;
                return (totalSuccessTime + currentDuration) / successCount.doubleValue();
            });
        }
    }
}
