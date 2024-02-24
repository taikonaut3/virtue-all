package io.github.taikonaut3.virtue.rpc.objectfactory;

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
}
