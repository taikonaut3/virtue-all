package io.github.taikonaut3.virtue.rpc.objectfactory;

import io.github.taikonaut3.virtue.event.Event;
import io.github.taikonaut3.virtue.rpc.objectfactory.listener.PooledObjectEvent;
import io.github.taikonaut3.virtue.rpc.objectfactory.listener.PooledObjectInvalidEvent;

/**
 * @author Chang Liu
 * 池中对象的状态
 */
public enum PooledObjectState {
    /**
     * 空闲
     */
    IDLE,
    /**
     * 无效
     */
    INVALID(){
        @Override
        public Event<PooledObject<?>> getEvent(PooledObject<?> pooledObject) {
            PooledObjectEvent<PooledObject<?>> pooledObjectEvent = new PooledObjectInvalidEvent<>();
            pooledObjectEvent.source(pooledObject);
            return pooledObjectEvent;
        }
    },
    /**
     * 正在使用
     */
    ALLOCATED;

    public Event<PooledObject<?>> getEvent(PooledObject<?> pooledObject){
        return null;
    }
}
