package io.virtue.event;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract EventDispatcher.
 */
public abstract class AbstractEventDispatcher implements EventDispatcher {

    protected final Map<Class<? extends Event<?>>, List<EventListener<?>>> listenerMap;

    protected AbstractEventDispatcher() {
        listenerMap = new ConcurrentHashMap<>();
    }

    @Override
    public <E extends Event<?>> void addListener(Class<E> eventType, EventListener<E> listener) {
        listenerMap.computeIfAbsent(eventType, k -> new LinkedList<>()).add(listener);
    }

    @Override
    public <E extends Event<?>> void removeListener(Class<E> eventType, EventListener<E> listener) {
        if (listenerMap.containsKey(eventType)) {
            List<EventListener<?>> listeners = listenerMap.get(eventType);
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                listenerMap.remove(eventType);
            }
        }
    }

    @Override
    public <E extends Event<?>> void dispatch(E event) {
        if (event != null && event.allowPropagation()) {
            doDispatch(event);
        }
    }

    protected abstract <E extends Event<?>> void doDispatch(E event);

}
