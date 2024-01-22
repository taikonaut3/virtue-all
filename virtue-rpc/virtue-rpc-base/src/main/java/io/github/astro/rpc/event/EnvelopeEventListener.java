package io.github.astro.rpc.event;

import io.github.astro.virtue.event.Event;
import io.github.astro.virtue.event.EventListener;

import java.util.concurrent.Executor;

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
