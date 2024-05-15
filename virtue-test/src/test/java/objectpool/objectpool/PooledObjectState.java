package objectpool.objectpool;

import io.virtue.event.Event;
import objectpool.objectpool.listener.PooledObjectEvent;
import objectpool.objectpool.listener.PooledObjectInvalidEvent;

/**
 * 池中对象的状态.
 *
 * @author Chang Liu
 */
public enum PooledObjectState {
    /**
     * 空闲.
     */
    IDLE,
    /**
     * 无效.
     */
    INVALID() {
        @Override
        public Event<PooledObject<?>> getEvent(PooledObject<?> pooledObject) {
            PooledObjectEvent<PooledObject<?>> pooledObjectEvent = new PooledObjectInvalidEvent<>();
            pooledObjectEvent.source(pooledObject);
            return pooledObjectEvent;
        }
    },
    /**
     * 正在使用.
     */
    ALLOCATED;

    public Event<PooledObject<?>> getEvent(PooledObject<?> pooledObject) {
        return null;
    }
}
