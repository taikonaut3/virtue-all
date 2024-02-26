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
     */
    void back(PooledObject<T> object);

    /**
     * Add an object to the pool.
     */
    void addObject();

    /**
     * Add multiple objects to the pool.
     *
     * @param count the number of objects to add
     **/
    default void addObjects(int count){
        if(count < 0){
            throw new IllegalArgumentException("Count must be greater than 0");
        }
        for (int i = 0; i < count; i++) {
            addObject();
        }
    }

    /**
     * Validate whether an object is still valid.
     *
     * @param pooledObject the object to validate
     */
    void validateObject(PooledObject<T> pooledObject);

    /**
     * Remove an object from the pool.
     *
     * @param object object
     * @return boolean is success
     */
    boolean remove(PooledObject<T> object);

    /**
     * pool object size
     *
     * @return size
     */
    int size();

}
