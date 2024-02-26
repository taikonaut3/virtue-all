package io.github.taikonaut3.virtue.rpc.objectfactory;

import java.util.concurrent.TimeUnit;

/**
 * @author Chang Liu
 */
public interface ObjectPool<T> {

    /**
     * Get an object from the pool, blocking until one is available.
     *
     * @return PooledObject<T>
     * @throws InterruptedException  if interrupted while waiting
     */
    PooledObject<T> poll() throws InterruptedException;

    /**
     * Get an object from the pool, waiting up to the specified wait time if necessary.
     *
     * @param time the maximum time to wait
     * @param timeUnit the time unit of the time argument
     * @return PooledObject<T>
     * @throws InterruptedException if interrupted while waiting
     * */
    PooledObject<T> poll(long time, TimeUnit timeUnit) throws InterruptedException;

    /**
     * Get an object from the pool, returning null if no objects are available.
     *
     * @return PooledObject<T> or null
     */
    PooledObject<T> get();

    /**
     * Back an object to the pool.
     *
     * @param object the object to back to the pool
     * @throws Exception back failed
     */
    void back(PooledObject<T> object) throws Exception;

    /**
     * Add an object to the pool.
     *
     * @throws Exception if the object creation fail
     */
    void addObject() throws Exception;

    /**
     * Add multiple objects to the pool.
     *
     * @param count the number of objects to add
     * @throws Exception if the object creation fails */
    default void addObjects(int count) throws Exception{
        if(count < 0){
            throw new IllegalArgumentException("count 必须大于 0");
        }
        for (int i = 0; i < count; i++) {
            addObject();
        }
    }

    /**
     * Validate whether an object is still valid.
     *
     * @param pooledObject the object to validate
     * @throws Exception if the object is not valid
     */
    void validateObject(PooledObject<T> pooledObject) throws Exception;

    /**
     * Remove an object from the pool.
     *
     * @param object
     * @return boolean is success
     * @throws Exception exception
     */
    boolean remove(PooledObject<T> object) throws Exception;

    /**
     * pool object size
     *
     * @return size
     */
    int size();

}
