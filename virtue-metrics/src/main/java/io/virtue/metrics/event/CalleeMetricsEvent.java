package io.virtue.metrics.event;

import io.virtue.metrics.CalleeMetrics;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Callee Metrics Event.
 */
@Getter
@Accessors(fluent = true)
public class CalleeMetricsEvent extends MetricsEvent<CalleeMetrics> {

    public CalleeMetricsEvent(CalleeMetrics calleeMetrics, boolean hasException, long currentDuration) {
        super(calleeMetrics, hasException, currentDuration);

    }

}
