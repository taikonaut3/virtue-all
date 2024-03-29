package io.virtue.event;

import io.virtue.common.extension.AbstractAccessor;

/**
 * Abstract Event.
 * @param <S>
 */
public abstract class AbstractEvent<S> extends AbstractAccessor implements Event<S> {

    protected S data;

    protected volatile boolean propagation = false;

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
    public void stopPropagation() {
        if (!propagation) {
            synchronized (this) {
                if (!propagation) {
                    this.propagation = true;
                }
            }
        }
    }

    @Override
    public boolean isPropagationStopped() {
        return propagation;
    }

}
