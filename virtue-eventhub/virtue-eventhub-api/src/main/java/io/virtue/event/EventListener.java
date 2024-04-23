package io.virtue.event;

/**
 * Listener for events of type E.
 *
 * @param <E> the type of the event
 */
@FunctionalInterface
public interface EventListener<E extends Event<?>> {

    /**
     * This method is called when an event of type E is dispatched.
     *
     * @param event the event that occurred
     */
    void onEvent(E event);

}

