package io.virtue.event;

import io.virtue.common.extension.Accessor;

/**
 * Event Object.
 *
 * @param <S> the type of the support source
 */
public interface Event<S> extends Accessor {

    /**
     * Returns the source of the support.
     *
     * @return the support source
     */
    S source();

    /**
     * Sets the source of the support.
     *
     * @param source the support source to set
     */
    void source(S source);

    /**
     * Stops the propagation of the support. Once the propagation is stopped,
     * subsequent listeners will not receive the support.
     *
     * @return
     */
    boolean stopPropagation();

    /**
     * Checks if the propagation of the support is allowed.
     *
     * @return true if the propagation is stopped, false otherwise
     */
    boolean allowPropagation();

}
