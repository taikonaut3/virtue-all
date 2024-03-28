package io.virtue.rpc.objectpool;

import java.util.concurrent.TimeUnit;

/**
 * @param <T>
 */
public interface ObjectPool<T> {

    /**
     * Get an object from the pool, blocking until one is available.
     *
     * @return
     * @throws InterruptedException
     */
    T poll() throws InterruptedException;

    /**
     * Get an object from the pool, waiting up to the specified wait time if necessary.
     * @param time
     * @param timeUnit
     * @return
     * @throws InterruptedException
     */
    T poll(long time, TimeUnit timeUnit) throws InterruptedException;

    /**
     * Get an object from the pool, returning null if no objects are available.
     *
     * @return T or null
     */
    T get();

    /**
     * Back an object to the pool.
     *
     * @param object the object to back to the pool
     */
    void back(T object);

    /**
     * Add an object to the pool.
     */
    void addObject();

    /**
     * Add multiple objects to the pool.
     *
     * @param count the number of objects to add
     **/
    default void addObjects(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count must be greater than 0");
        }
        for (int i = 0; i < count; i++) {
            addObject();
        }
    }

    /**
     * Validate whether an object is still valid.
     *
     * @param object to validate
     */
    void validateObject(T object);

    /**
     * Remove an object from the pool.
     *
     * @param object object
     */
    void destroy(T object);

    /**
     * pool object size.
     *
     * @return size
     */
    int size();

}
