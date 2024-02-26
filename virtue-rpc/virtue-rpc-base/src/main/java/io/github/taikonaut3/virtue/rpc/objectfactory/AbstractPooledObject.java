package io.github.taikonaut3.virtue.rpc.objectfactory;

import io.github.taikonaut3.virtue.common.util.CollectionUtil;
import io.github.taikonaut3.virtue.rpc.objectfactory.listener.PooledObjectEvent;
import io.github.taikonaut3.virtue.rpc.objectfactory.listener.PooledObjectListener;

import java.time.Instant;
import java.util.*;
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
    @SafeVarargs
    @Override
    public final void addListener(PooledObjectState state, PooledObjectListener<PooledObjectEvent<PooledObject<?>>>... eventListener){
        if(Objects.isNull(eventListener) || eventListener.length == 0){
            throw new IllegalArgumentException("listener must be present");
        }
        List<PooledObjectListener<PooledObjectEvent<PooledObject<?>>>> listenerList = listenerMap.computeIfAbsent(state,key -> new ArrayList<>());
        listenerList.addAll(Arrays.asList(eventListener));
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
        PooledObjectEvent<PooledObject<?>> event = (PooledObjectEvent<PooledObject<?>>) state.getEvent(this);
        if(Objects.isNull(event)){
            return;
        }
        List<PooledObjectListener<PooledObjectEvent<PooledObject<?>>>> listenerList = listenerMap.get(state);
        if(CollectionUtil.isEmpty(listenerList)){
            return;
        }
        listenerList.forEach(listener -> listener.onEvent(event));
    }

}
