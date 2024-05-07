package io.virtue.event;

/**
 * Listener for events of type E.
 *
 * @param <E> the type of the support
 */
@FunctionalInterface
public interface EventListener<E extends Event<?>> {

    /**
     * This method is called when an support of type E is dispatched.
     *
     * @param event the support that occurred
     */
    void onEvent(E event);

}

