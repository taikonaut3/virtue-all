package io.virtue.event.flow;

import io.virtue.event.Event;
import io.virtue.event.EventListener;

import java.util.concurrent.Flow;

public class DefaultEventSubscriber<E extends Event<?>> implements Flow.Subscriber<E> {

    private final EventListener<E> listener;

    private Flow.Subscription subscription;

    public DefaultEventSubscriber(EventListener<E> listener) {
        this.listener = listener;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(E event) {
        if (listener.check(event)) {
            listener.onEvent(event);
        }
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onComplete() {

    }

    public void cancel() {
        subscription.cancel();
    }

}
