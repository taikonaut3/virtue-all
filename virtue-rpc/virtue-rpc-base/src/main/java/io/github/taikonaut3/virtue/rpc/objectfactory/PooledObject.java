package io.github.taikonaut3.virtue.rpc.objectfactory;

import io.github.taikonaut3.virtue.rpc.objectfactory.listener.PooledObjectEvent;
import io.github.taikonaut3.virtue.rpc.objectfactory.listener.PooledObjectListener;

import java.time.Instant;

/**
 * @author Chang Liu
 */
public interface PooledObject<T>{
    /**
     * 获取 真实对象
     * @return 真实对象
     */
    T getObject();

    /**
     * 返回 对象的创建时间
     * @return 创建时间
     */
    Instant getCreateInstant();

    /**
     * 获取当前对象的状态
     * @return PooledObjectState
     */
    PooledObjectState getState();

    /**
     * 设置对象的状态
     * @param state PooledObjectState
     */
    void setState(PooledObjectState state);

    /**
     * Add a listener with state as the key
     * @param state state
     * @param eventListener eventListeners
     */
    void addListener(PooledObjectState state, PooledObjectListener<PooledObjectEvent<PooledObject<?>>>...eventListener);
}
