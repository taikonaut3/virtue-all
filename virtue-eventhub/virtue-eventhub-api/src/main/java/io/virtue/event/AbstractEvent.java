package io.virtue.event;

import io.virtue.common.extension.AbstractAccessor;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract Event.
 *
 * @param <S>
 */
public abstract class AbstractEvent<S> extends AbstractAccessor implements Event<S> {

    protected S data;

    protected AtomicBoolean propagation = new AtomicBoolean(true);

    protected AbstractEvent() {

    }

    protected AbstractEvent(S data) {
        this.data = data;
    }

    @Override
    public S source() {
        return data;
    }

    @Override
    public void source(S source) {
        this.data = source;
    }

    @Override
    public boolean stopPropagation() {
        return propagation.compareAndSet(true, false);
    }

    @Override
    public boolean allowPropagation() {
        return propagation.get();
    }

}
