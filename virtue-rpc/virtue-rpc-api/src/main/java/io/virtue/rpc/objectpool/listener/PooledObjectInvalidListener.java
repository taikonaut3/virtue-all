package io.virtue.rpc.objectpool.listener;


import io.virtue.rpc.objectpool.PooledObject;


/**
 * Executed when the object is set to invalid.
 * @param <S>
 */
public class PooledObjectInvalidListener<S extends PooledObjectEvent<PooledObject<?>>> extends PooledObjectListener<S> {


    @Override
    public void onEvent(S event) {

    }
}
