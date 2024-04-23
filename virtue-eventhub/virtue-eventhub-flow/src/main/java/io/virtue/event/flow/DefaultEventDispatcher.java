package io.virtue.event.flow;

import io.virtue.common.constant.Components;
import io.virtue.common.spi.Extension;
import io.virtue.event.AbstractEventDispatcher;
import io.virtue.event.Event;
import io.virtue.event.EventDispatcher;
import io.virtue.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;

@Extension(Components.EventDispatcher.FLOW)
public class DefaultEventDispatcher extends AbstractEventDispatcher implements EventDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(DefaultEventDispatcher.class);

    private final Map<EventListener<?>, DefaultEventSubscriber<?>> subscriberMap = new ConcurrentHashMap<>();

    private final DefaultPublisher<Event<?>> publisher;

    public DefaultEventDispatcher() {
        publisher = new DefaultPublisher<>();
    }

    public DefaultEventDispatcher(Executor executor) {
        publisher = new DefaultPublisher<>(executor);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Event<?>> void addListener(Class<E> eventType, EventListener<E> listener) {
        super.addListener(eventType, listener);
        DefaultEventSubscriber<?> subscriber = new DefaultEventSubscriber<>(listener);
        publisher.subscribe((Flow.Subscriber<? super Event<?>>) subscriber);
        subscriberMap.put(listener, subscriber);
        logger.debug("Register Listener for Event[{}],Listener: {}", eventType.getSimpleName(), listener.getClass().getSimpleName());
    }

    @Override
    public <E extends Event<?>> void removeListener(Class<E> eventType, EventListener<E> listener) {
        super.removeListener(eventType, listener);
        DefaultEventSubscriber<?> subscriber = subscriberMap.get(listener);
        subscriber.cancel();
        logger.debug("Remove Listener for Event[{}],Listener: {}", eventType.getSimpleName(), listener.getClass().getSimpleName());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <E extends Event<?>> void doDispatch(E event) {
        List<EventListener<?>> listeners = listenerMap.entrySet().stream()
                .filter(entry -> entry.getKey().isAssignableFrom(event.getClass()))
                .flatMap(entry -> entry.getValue().stream())
                .toList();
        for (EventListener<?> listener : listeners) {
            logger.debug("Publish Event(" + event + ") to " + listener);
            publisher.publish(event, (Flow.Subscriber<? super Event<?>>) subscriberMap.get(listener));
        }
    }

}
