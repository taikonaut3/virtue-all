package io.github.taikonaut3.virtue.rpc.objectfactory;

import io.github.taikonaut3.virtue.event.Event;
import io.github.taikonaut3.virtue.event.EventListener;
import io.github.taikonaut3.virtue.rpc.objectfactory.listener.PooledObjectEvent;
import io.github.taikonaut3.virtue.rpc.objectfactory.listener.PooledObjectInvalidListener;
import io.github.taikonaut3.virtue.rpc.objectfactory.listener.PooledObjectListener;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Chang Liu
 */
public class DefaultPooledObject<T> extends AbstractPooledObject<T>{


    @SuppressWarnings("unchecked")
    private final Map<PooledObjectState,List<PooledObjectListener<PooledObjectEvent<PooledObject<?>>>>> listenerMap = new ConcurrentHashMap<>(){
        {
            addListener(PooledObjectState.INVALID, new PooledObjectInvalidListener<>());
        }
    };

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
        List<PooledObjectListener<PooledObjectEvent<PooledObject<?>>>> listenerList = listenerMap.get(state);
        if(CollectionUtils.isEmpty(listenerList)){
            return;
        }
        PooledObjectEvent<PooledObject<?>> event = (PooledObjectEvent<PooledObject<?>>) state.getEvent(this);
        listenerList.forEach(listener -> listener.onEvent(event));
    }

    public void addListener(PooledObjectState state, PooledObjectListener<PooledObjectEvent<PooledObject<?>>>...eventListener){
        if(Objects.isNull(eventListener) || eventListener.length == 0){
            throw new IllegalArgumentException("listener must be present");
        }
        List<PooledObjectListener<PooledObjectEvent<PooledObject<?>>>> listenerList = this.listenerMap.computeIfAbsent(state,key -> new ArrayList<>());
        listenerList.addAll(Arrays.asList(eventListener));
    }
}
