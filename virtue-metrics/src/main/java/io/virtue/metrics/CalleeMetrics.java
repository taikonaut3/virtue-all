package io.virtue.metrics;

import io.virtue.common.extension.AttributeKey;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

/**
 * Callee Metrics.
 */
@Data
@Accessors(fluent = true)
public class CalleeMetrics {

    public static final AttributeKey<CalleeMetrics> ATTRIBUTE_KEY = AttributeKey.of("calleeMetrics");

    private LongAdder requestCount = new LongAdder();

    private LongAdder successCount = new LongAdder();

    private LongAdder failureCount = new LongAdder();

    private AtomicReference<Double> averageResponseTime = new AtomicReference<>(0.0);

    private AtomicLong maxResponseTime = new AtomicLong();

    private AtomicLong minResponseTime = new AtomicLong();

}
