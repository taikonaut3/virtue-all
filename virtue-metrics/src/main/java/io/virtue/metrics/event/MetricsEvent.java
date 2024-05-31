package io.virtue.metrics.event;

import io.virtue.event.AbstractEvent;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Metrics Event.
 *
 * @param <T>
 */
@Getter
@Accessors(fluent = true)
public class MetricsEvent<T> extends AbstractEvent<T> {

    private final boolean hasException;

    private final long currentDuration;

    public MetricsEvent(T source, boolean hasException, long currentDuration) {
        super(source);
        this.hasException = hasException;
        this.currentDuration = currentDuration;
    }
}
