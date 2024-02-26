package io.github.taikonaut3.virtue.rpc.objectfactory;

import io.github.taikonaut3.virtue.rpc.objectfactory.listener.PooledObjectEvent;
import io.github.taikonaut3.virtue.rpc.objectfactory.listener.PooledObjectListener;

import java.time.Instant;
import java.util.*;

/**
 * @author Chang Liu
 */
public class DefaultPooledObject<T> extends AbstractPooledObject<T>{


    public DefaultPooledObject(final T object){
        super(object);
    }

    @Override
    public T getObject() {
        return object;
    }

    @Override
    public Instant getCreateInstant() {
        return createInstant;
    }

    @Override
    public PooledObjectState getState() {
        return state;
    }

    @Override
    public void setState(PooledObjectState state) {
        this.state = state;
        PooledObjectEvent<PooledObject<?>> event = (PooledObjectEvent<PooledObject<?>>) state.getEvent(this);
        if(Objects.isNull(event)){
            return;
        }
        List<PooledObjectListener<PooledObjectEvent<PooledObject<?>>>> listenerList = listenerMap.get(state);
        if(CollectionUtils.isEmpty(listenerList)){
            return;
        }
        listenerList.forEach(listener -> listener.onEvent(event));
    }

    @Override
    public void addListener(PooledObjectState state, PooledObjectListener<PooledObjectEvent<PooledObject<?>>>...eventListener){
        if(Objects.isNull(eventListener) || eventListener.length == 0){
            throw new IllegalArgumentException("listener must be present");
        }
        List<PooledObjectListener<PooledObjectEvent<PooledObject<?>>>> listenerList = listenerMap.computeIfAbsent(state,key -> new ArrayList<>());
        listenerList.addAll(Arrays.asList(eventListener));
    }
}
