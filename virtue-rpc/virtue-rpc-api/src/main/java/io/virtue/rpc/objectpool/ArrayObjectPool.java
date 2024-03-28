package io.virtue.rpc.objectpool;

import io.virtue.common.util.AssertUtil;
import io.virtue.core.Virtue;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * ArrayObjectPool.
 * @param <T>
 */
@SuppressWarnings("unchecked")
public class ArrayObjectPool<T> extends AbstractObjectPool<T> {
    private final PooledObject<T>[] pooledObjectArr;
    private final ReentrantLock mainLock = new ReentrantLock();
    private final Condition available = mainLock.newCondition();
    private static final String LOCK_KEY_FORMAT = "pool-lock-%d";

    public ArrayObjectPool(Virtue virtue, PooledObjectFactory<T> factory, ObjectPoolConfig poolConfig) {
        super(virtue, factory, poolConfig);
        pooledObjectArr = new PooledObject[poolConfig.initCapacity()];
        addObjects(poolConfig.minIdle());
    }

    public ArrayObjectPool(Virtue virtue, PooledObjectFactory<T> factory) {
        this(virtue, factory, new ObjectPoolConfig());
    }

    @Override
    public T poll() throws InterruptedException {
        T t = get();
        if (Objects.nonNull(t)) {
            return t;
        }
        mainLock.lock();
        try {
            // keep waiting
            while (Objects.isNull(t)) {
                available.await();
                t = get();
            }
        } finally {
            mainLock.unlock();
        }
        return t;
    }

    private T doGet() {
        for (int i = 0; i < size; i++) {
            if (pooledObjectArr[i].state() == PooledObjectState.IDLE) {
                synchronized (generateMemoryLockKey(i).intern()) {
                    if (pooledObjectArr[i].state() == PooledObjectState.IDLE) {
                        pooledObjectArr[i].state(PooledObjectState.ALLOCATED);
                        return pooledObjectArr[i].getObject();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public T poll(long time, TimeUnit timeUnit) throws InterruptedException {
        T t = get();
        if (Objects.nonNull(t)) {
            return t;
        }
        long deadLine = timeUnit.toNanos(time);
        mainLock.lock();
        try {
            while (Objects.isNull(t)) {
                long remaining = deadLine - System.nanoTime();
                if (remaining <= 0L) {
                    return null;
                }
                remaining = available.awaitNanos(timeUnit.toNanos(remaining));
                if (remaining <= 0L) {
                    return null;
                }
                t = get();
            }
        } finally {
            mainLock.unlock();
        }
        return t;
    }

    @Override
    public T get() {
        boolean isFull = Arrays.stream(pooledObjectArr)
                .filter(Objects::nonNull)
                .allMatch(pooledObject -> pooledObject.state() == PooledObjectState.ALLOCATED);
        if (isFull) {
            if (size == poolConfig.initCapacity()) {
                return null;
            }
            addObject();
        }
        return doGet();
    }

    @Override
    public void back(T object) {
        validateObject(object);
        for (int i = 0; i < size; i++) {
            PooledObject<T> pooledObject = pooledObjectArr[i];
            if (pooledObject.getObject() == object) {
                pooledObjectArr[i].state(PooledObjectState.IDLE);
                break;
            }
        }
        mainLock.lock();
        try {
            available.signal();
        } finally {
            mainLock.unlock();
        }
    }

    private String generateMemoryLockKey(int index) {
        return String.format(LOCK_KEY_FORMAT, index);
    }

    @Override
    public void addObject() {
        PooledObject<T> pooledObject = factory.makeObject();
        AssertUtil.notNull(pooledObject);
        validateObject(pooledObject.getObject());
        mainLock.lock();
        try {
            if (size == poolConfig.initCapacity()) {
                throw new RuntimeException("Pool capacity is full and cannot continue to be added");
            }
            pooledObjectArr[size] = pooledObject;
            pooledObject.state(PooledObjectState.IDLE);
            available.signal();
            size++;
            createdCount.incrementAndGet();
        } finally {
            mainLock.unlock();
        }
    }

    @Override
    public void validateObject(T object) {
        if (Objects.isNull(object)) {
            throw new NullPointerException();
        }
    }

    @Override
    public void destroy(T object) {
        validateObject(object);
        mainLock.lock();
        try {
            for (int i = 0; i < size; i++) {
                PooledObject<T> pooledObject = pooledObjectArr[i];
                if (pooledObject.getObject() == object) {
                    fastRemove(pooledObjectArr, i, () -> null);
                    pooledObject.state(PooledObjectState.INVALID);
                    destroyObject(pooledObject);
                }
            }
        } finally {
            mainLock.unlock();
        }
    }

    private void destroyObject(PooledObject<T> pooledObject) {
        try {
            factory.destroyObject(pooledObject);
        } finally {
            destroyCount.incrementAndGet();
        }
    }

    private void fastRemove(PooledObject<T>[] pooledObjectArr, int index, Supplier<PooledObject<T>> defaultValue) {
        int newSize;
        // is not last element
        if ((newSize = size - 1) > index) {
            System.arraycopy(pooledObjectArr, index + 1, pooledObjectArr, index, newSize - index);
        }
        pooledObjectArr[size = newSize] = defaultValue.get();
    }

}
