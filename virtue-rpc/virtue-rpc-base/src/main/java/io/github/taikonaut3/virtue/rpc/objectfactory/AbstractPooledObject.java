package io.github.taikonaut3.virtue.rpc.objectfactory;

import java.time.Instant;

/**
 * @author xiaoquan
 * @date 2024/2/25 14:14
 */
public abstract class AbstractPooledObject<T> implements PooledObject<T>{
    protected final T object;
    protected final Instant createInstant = Instant.now();
    protected PooledObjectState state = PooledObjectState.IDLE;

    protected AbstractPooledObject(T object){
        this.object = object;
    }
}
