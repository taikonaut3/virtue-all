package objectpool.objectpool;

import io.virtue.common.util.AssertUtil;
import io.virtue.core.Virtue;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Abstract ObjectPool.
 *
 * @param <T>
 */
public abstract class AbstractObjectPool<T> implements ObjectPool<T> {

    protected final Virtue virtue;
    protected final PooledObjectFactory<T> factory;
    protected final ObjectPoolConfig poolConfig;
    protected final AtomicLong createdCount = new AtomicLong();
    protected final AtomicLong destroyCount = new AtomicLong();
    protected int size;

    protected AbstractObjectPool(Virtue virtue, PooledObjectFactory<T> factory, ObjectPoolConfig poolConfig) {
        AssertUtil.notNull(virtue, factory, poolConfig);
        this.virtue = virtue;
        this.factory = factory;
        this.poolConfig = poolConfig;
        init();
    }

    protected void before() {
    }

    protected void after(PooledObject<T> pooledObject) {
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * 初始化 object.
     */
    private void init() {
        int capacity = poolConfig.initCapacity();
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity cannot be less than 0");
        }
        int minIdle = poolConfig.minIdle();
        if (minIdle < 0) {
            throw new IllegalArgumentException("minIdle cannot be less than 0");
        }
        if (minIdle > capacity) {
            throw new IllegalArgumentException("minIdle cannot be more than capacity");
        }
    }
}