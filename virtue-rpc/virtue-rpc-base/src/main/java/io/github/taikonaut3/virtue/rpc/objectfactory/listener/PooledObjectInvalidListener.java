package io.github.taikonaut3.virtue.rpc.objectfactory.listener;


import io.github.taikonaut3.virtue.rpc.objectfactory.PooledObject;

/**
 * @author Chang Liu
 * 对象在置为无效时执行的 listener
 */
public class PooledObjectInvalidListener<S extends PooledObjectInvalidEvent<PooledObject<?>>> extends PooledObjectListener<S> {


    @Override
    public void onEvent(S event) {

    }
}
