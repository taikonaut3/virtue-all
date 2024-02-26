package io.github.taikonaut3.virtue.rpc.objectfactory;

import io.github.taikonaut3.virtue.rpc.objectfactory.listener.PooledObjectEvent;
import io.github.taikonaut3.virtue.rpc.objectfactory.listener.PooledObjectInvalidListener;
import io.github.taikonaut3.virtue.rpc.objectfactory.listener.PooledObjectListener;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiaoquan
 * @date 2024/2/25 14:14
 */
public abstract class AbstractPooledObject<T> implements PooledObject<T>{

    protected final T object;
    protected final Instant createInstant = Instant.now();
    protected PooledObjectState state = PooledObjectState.IDLE;

    protected final Map<PooledObjectState, List<PooledObjectListener<PooledObjectEvent<PooledObject<?>>>>> listenerMap = new ConcurrentHashMap<>();

    protected AbstractPooledObject(T object){
        this.object = object;
    }

    @Override
    public void addListener(PooledObjectState state, PooledObjectListener<PooledObjectEvent<PooledObject<?>>>... eventListener) {
        throw new UnsupportedOperationException();
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

    @Override
    public void state(PooledObjectState state) {
        this.state = state;
    }
}
