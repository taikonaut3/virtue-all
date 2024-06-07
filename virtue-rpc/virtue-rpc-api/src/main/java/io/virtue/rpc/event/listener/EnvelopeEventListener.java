package io.virtue.rpc.event.listener;

import io.virtue.event.Event;
import io.virtue.event.EventListener;

import java.util.concurrent.ExecutorService;

/**
 * Abstract envelope support listener.
 *
 * @param <T>
 */
public abstract class EnvelopeEventListener<T extends Event<?>> implements EventListener<T> {

    protected ExecutorService executor;

    public EnvelopeEventListener(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void onEvent(T event) {
        if (!executor.isShutdown()) {
            executor.execute(() -> handEnvelopeEvent(event));
        } else {
            jvmShuttingDown(event);
        }
    }

    protected abstract void handEnvelopeEvent(T event);

    protected abstract void jvmShuttingDown(T event);

}
