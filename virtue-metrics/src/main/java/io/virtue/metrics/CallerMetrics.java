package io.virtue.metrics;

import io.virtue.common.extension.AttributeKey;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

/**
 * Caller Metrics.
 */
@Data
@Accessors(fluent = true)
public class CallerMetrics {

    public static final AttributeKey<CallerMetrics> ATTRIBUTE_KEY = AttributeKey.of("callerMetrics");

    private LongAdder callCount = new LongAdder();

    private LongAdder successCallCount = new LongAdder();

    private LongAdder failureCallCount = new LongAdder();

    private AtomicReference<Double> averageCallTime = new AtomicReference<>(0.0);

    private AtomicLong maxCallTime = new AtomicLong();

    private AtomicLong minCallTime = new AtomicLong();

    private LongAdder retryCount = new LongAdder();

    private LongAdder timeoutCount = new LongAdder();

}
