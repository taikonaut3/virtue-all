package io.github.taikonaut3.virtue.rpc.objectpool;

import java.time.Instant;

/**
 * @author xiaoquan
 * @date 2024/2/25 14:14
 */
public abstract class AbstractPooledObject<T> implements PooledObject<T>{

    protected final T object;
    protected final Instant createInstant = Instant.now();
    protected volatile PooledObjectState state = PooledObjectState.IDLE;

    protected AbstractPooledObject(T object){
        this.object = object;
    }

    @Override
    public T getObject() {
        return object;
    }

    @Override
    public Instant createInstant() {
        return createInstant;
    }

    @Override
    public PooledObjectState state() {
        return state;
    }

}
