package io.github.taikonaut3.virtue.rpc.objectfactory;

/**
 * @author Chang Liu
 */
public abstract class AbstractObjectPool<T> implements ObjectPool<T> {
    protected final PooledObjectFactory<T> factory;

    protected final ObjectPoolConfig poolConfig;

    protected int size;
    protected AbstractObjectPool(PooledObjectFactory<T> factory,ObjectPoolConfig poolConfig){
        this.factory = factory;
        this.poolConfig = poolConfig;
    }

    protected void before(){}

    protected void after(PooledObject<T> pooledObject){}

    @Override
    public int size() {
        return size;
    }
}