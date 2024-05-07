package io.virtue.rpc.listener;

import io.virtue.event.Event;
import io.virtue.event.EventListener;

import java.util.concurrent.Executor;

/**
 * Abstract envelope support listener.
 *
 * @param <T>
 */
public abstract class EnvelopeEventListener<T extends Event<?>> implements EventListener<T> {

    protected Executor executor;

    public EnvelopeEventListener(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void onEvent(T event) {
        executor.execute(() -> handEnvelopeEvent(event));
    }

    protected abstract void handEnvelopeEvent(T event);

}
