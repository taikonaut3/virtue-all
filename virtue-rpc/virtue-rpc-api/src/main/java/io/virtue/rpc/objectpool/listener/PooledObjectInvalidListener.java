package io.virtue.rpc.objectpool.listener;


import io.virtue.rpc.objectpool.PooledObject;

/**
 * @author Chang Liu
 * 对象在置为无效时执行的 listener
 */
public class PooledObjectInvalidListener<S extends PooledObjectEvent<PooledObject<?>>> extends PooledObjectListener<S> {


    @Override
    public void onEvent(S event) {

    }
}
