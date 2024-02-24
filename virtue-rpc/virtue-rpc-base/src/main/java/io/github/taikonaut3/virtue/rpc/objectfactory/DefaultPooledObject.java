package io.github.taikonaut3.virtue.rpc.objectfactory;

import io.github.taikonaut3.virtue.event.Event;
import io.github.taikonaut3.virtue.event.EventListener;
import io.github.taikonaut3.virtue.rpc.objectfactory.listener.PooledObjectInvalidListener;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Chang Liu
 */
public class DefaultPooledObject<T> implements PooledObject<T>{

    private final T object;
    private final Instant createInstant = Instant.now();
    private PooledObjectState state = PooledObjectState.IDLE;


    private final Map<PooledObjectState,List<EventListener<Event<?>>>> listenerMap = new ConcurrentHashMap<>(){
        {
            addListener(PooledObjectState.INVALID, new PooledObjectInvalidListener());
        }
    };


    public DefaultPooledObject(final T object){
        this.object = object;
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
        List<EventListener<Event<?>>> listenerList = listenerMap.get(state);
        if(CollectionUtils.isEmpty(listenerList)){
            return;
        }
        Event<?> event = state.getEvent(this);
        listenerList.forEach(listener -> listener.onEvent(event));
    }

    public void addListener(PooledObjectState state,EventListener<Event<?>>...eventListener){
        if(Objects.isNull(eventListener) || eventListener.length == 0){
            throw new IllegalArgumentException("listener must be present");
        }
        List<EventListener<Event<?>>> listenerList = this.listenerMap.computeIfAbsent(state,key -> new ArrayList<>());
        listenerList.addAll(Arrays.asList(eventListener));
    }
}
