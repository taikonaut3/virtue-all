package io.github.taikonaut3.virtue.rpc.objectfactory;

import java.util.concurrent.TimeUnit;

/**
 * @author Chang Liu
 */
public interface ObjectPool<T> {
    /**
     * 获取对象，如果没有可用对象，会一直等待
     * @return T
     * @throws InterruptedException 中断异常
     */
    T poll() throws InterruptedException;

    /**
     * 获取对象 带有超时
     * @param millis 等待时间
     * @param timeUnit 时间单位
     * @return T
     * @throws InterruptedException 中断异常
     */
    T poll(long millis, TimeUnit timeUnit) throws InterruptedException;

    /**
     * 获取对象，没有可用对象返回null
     * @return T or null
     */
    T get();

    /**
     * 归还对象
     * @param object 待归还的对象
     */
    void back(T object);

    /**
     * 添加一个对象
     * @throws Exception 创建对象失败
     */
    void addObject() throws Exception;

    /**
     * 添加多个对象
     * @param count 对象的个数
     * @throws Exception 创建对象失败
     */
    default void addObjects(int count) throws Exception{
        if(count < 0){
            throw new IllegalArgumentException("count 必须大于 0");
        }
        for (int i = 0; i < count; i++) {
            addObject();
        }
    }

    /**
     * 校验对象是否合法
     * @param pooledObject 校验 添加的对象是否合法
     * @throws Exception 不合法抛出
     */
    void validateObject(PooledObject<T> pooledObject) throws Exception;

    /**
     * 删除对象
     * @param object
     * @return pooledObject
     * @throws Exception exception
     */
    PooledObject<?> remove(PooledObject<?> object) throws Exception;

}
