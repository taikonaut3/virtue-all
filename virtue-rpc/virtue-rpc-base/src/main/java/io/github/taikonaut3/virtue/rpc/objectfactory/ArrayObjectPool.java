package io.github.taikonaut3.virtue.rpc.objectfactory;

import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * ArrayObjectPool
 * @author Chang Liu
 */
@SuppressWarnings("unchecked")
public class ArrayObjectPool<T> extends AbstractObjectPool<T>{
    private final PooledObject<T>[] pooledObjectArr;
    private final ReentrantLock mainLock = new ReentrantLock();
    private final Condition notEmpty = mainLock.newCondition();
    private static final String LOCK_KEY_FORMAT = "pool-lock-%d";

    public ArrayObjectPool(PooledObjectFactory<T> factory,ObjectPoolConfig poolConfig){
        super(factory,poolConfig);
        int capacity = poolConfig.initCapacity();
        if(capacity < 0){
            throw new IllegalArgumentException("capacity cannot be less than 0");
        }
        if(Objects.isNull(factory)){
            throw new NullPointerException();
        }
        pooledObjectArr = new PooledObject[capacity];
        init();
    }

    public ArrayObjectPool(PooledObjectFactory<T> factory){
        this(factory,ObjectPoolConfig.getDefault());
    }

    @Override
    public T poll() throws InterruptedException{
        T t = get();
        if(Objects.nonNull(t)){
            return t;
        }
        mainLock.lock();
        try{
            // keep waiting
            while(Objects.isNull(t)){
                notEmpty.wait();
                t = get();
            }
        }finally {
            mainLock.unlock();
        }
        return t;
    }
    private T doGet(){
        for (int i = 0; i < size; i++) {
            if (pooledObjectArr[i].state() == PooledObjectState.IDLE) {
                synchronized(generateMemoryLockKey(i).intern()){
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
        if(Objects.nonNull(t)){
            return t;
        }
        long deadLine = timeUnit.toNanos(time);
        mainLock.lock();
        try{
            while(Objects.isNull(t)){
                long remaining = deadLine - System.nanoTime();
                if(remaining <= 0L){
                    return null;
                }
                remaining = notEmpty.awaitNanos(timeUnit.toNanos(remaining));
                if(remaining <= 0L){
                    return null;
                }
                t = get();
            }
        }finally {
            mainLock.unlock();
        }
        return t;
    }

    @Override
    public T get() {
        boolean isFull = Arrays.stream(pooledObjectArr).allMatch(pooledObject -> pooledObject.state() == PooledObjectState.ALLOCATED);
        if(isFull){
            if(size == poolConfig.initCapacity()){
               return null;
            }
            addObject();
        }
        return doGet();
    }

    @Override
    public void back(T object){
        validateObject(object);
        for (int i = 0; i < size; i++) {
            PooledObject<T> pooledObject = pooledObjectArr[i];
            if(pooledObject.getObject() == object){
                pooledObjectArr[i].state(PooledObjectState.IDLE);
                break;
            }
        }
        mainLock.lock();
        try{
            notEmpty.notify();
        }finally {
            mainLock.unlock();
        }
    }

    private String generateMemoryLockKey(int index){
        return String.format(LOCK_KEY_FORMAT,index);
    }

    @Override
    public void addObject(){
        PooledObject<T> pooledObject = factory.makeObject();
        if(Objects.isNull(pooledObject)){
            throw new NullPointerException();
        }
        validateObject(pooledObject.getObject());
        mainLock.lock();
        try{
            if (size == poolConfig.initCapacity()) {
                throw new RuntimeException("Pool capacity is full and cannot continue to be added");
            }
            pooledObjectArr[size] = pooledObject;
            pooledObject.state(PooledObjectState.IDLE);
            notEmpty.notify();
            size++;
            createdCount.incrementAndGet();
        }finally {
            mainLock.unlock();
        }
    }

    @Override
    public void validateObject(T object){
        if(Objects.isNull(object)){
            throw new NullPointerException();
        }
    }

    @Override
    public void destroy(T object){
        validateObject(object);
        mainLock.lock();
        try{
            for (int i = 0; i < size; i++) {
                PooledObject<T> pooledObject = pooledObjectArr[i];
                if(pooledObject.getObject() == object){
                    fastRemove(pooledObjectArr,i,() -> null);
                    pooledObject.state(PooledObjectState.INVALID);
                    destroyObject(pooledObject);
                }
            }
        }finally {
            mainLock.unlock();
        }
    }

    private void destroyObject(PooledObject<T> pooledObject){
        try{
            factory.destroyObject(pooledObject);
        }finally {
            destroyCount.incrementAndGet();
        }
    }

    private void fastRemove(PooledObject<T>[] pooledObjectArr, int index, Supplier<PooledObject<T>> defaultValue){
       int newSize;
       // is not last element
       if((newSize = size - 1) > index){
           System.arraycopy(pooledObjectArr,index + 1,pooledObjectArr,index,newSize - index);
       }
        pooledObjectArr[size = newSize] = defaultValue.get();
    }

    /**
     * 初始化 object
     */
    private void init(){
        int minIdle = poolConfig.minIdle();
        int capacity = poolConfig.initCapacity();
        if(minIdle < 0){
            throw new IllegalArgumentException("minIdle cannot be less than 0");
        }
        if(minIdle > capacity){
            throw new IllegalArgumentException("minIdle cannot be more than capacity");
        }
        addObjects(minIdle);
    }
}
