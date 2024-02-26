package io.github.taikonaut3.virtue.rpc.objectfactory;

import io.github.taikonaut3.virtue.rpc.objectfactory.listener.PooledObjectEvent;
import io.github.taikonaut3.virtue.rpc.objectfactory.listener.PooledObjectListener;

import java.time.Instant;

/**
 * @author Chang Liu
 */
public interface PooledObject<T>{
    /**
     * Get the underlying object.
     *
     * @return the underlying object
     */
    T getObject();

    /**
     * Return the creation time of the object.
     *
     * @return the creation time
     */
    Instant createInstant();

    /**
     * Get the current state of the object.
     *
     * @return PooledObjectState
     */
    PooledObjectState state();

    /**
     * Set the state of the object.
     *
     * @param state PooledObjectState
     */
    void state(PooledObjectState state);

    /**
     * Add a listener with state as the key
     *
     * @param state state
     * @param eventListener eventListeners
     */
    void addListener(PooledObjectState state, PooledObjectListener<PooledObjectEvent<PooledObject<?>>>...eventListener);
}
