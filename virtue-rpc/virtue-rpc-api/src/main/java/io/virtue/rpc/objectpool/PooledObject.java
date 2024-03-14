package io.virtue.rpc.objectpool;

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

}
