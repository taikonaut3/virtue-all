package io.github.taikonaut3.virtue.rpc.objectfactory;

/**
 * @author Chang Liu
 */
public abstract class AbstractObjectPool<T> implements ObjectPool<T> {
    protected final PooledObjectFactory<T> factory;

    protected AbstractObjectPool(PooledObjectFactory<T> factory){
        this.factory = factory;
    }

    protected void before(){}

    protected void after(PooledObject<T> pooledObject){}
}