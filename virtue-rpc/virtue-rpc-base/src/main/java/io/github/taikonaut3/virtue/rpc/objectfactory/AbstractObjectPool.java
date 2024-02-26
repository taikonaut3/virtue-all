package io.github.taikonaut3.virtue.rpc.objectfactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Chang Liu
 */
public abstract class AbstractObjectPool<T> implements ObjectPool<T> {
    protected final PooledObjectFactory<T> factory;

    protected final ObjectPoolConfig poolConfig;

    protected final AtomicLong createdCount = new AtomicLong();

    protected final AtomicLong destroyCount = new AtomicLong();

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