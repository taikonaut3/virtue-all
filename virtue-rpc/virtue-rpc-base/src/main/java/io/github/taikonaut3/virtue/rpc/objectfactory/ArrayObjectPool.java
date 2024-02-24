package io.github.taikonaut3.virtue.rpc.objectfactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Chang Liu
 */
public class ArrayObjectPool<T> extends AbstractObjectPool<T>{

    private final Object[] objectArr;
    private final int[] objectStatus;
    private final ReentrantLock mainLock = new ReentrantLock();
    private final Condition notFull = mainLock.newCondition();
    private static final int USE = 1;
    private static final int NOT_USE = 0;
    private static final String LOCK_KEY_FORMAT = "pool-lock-%d";
    private int currentIndex;

    private final ObjectPoolConfig poolConfig;


    public ArrayObjectPool(ObjectPoolConfig poolConfig,PooledObjectFactory<T> factory) throws Exception {
        super(factory);
        int capacity = poolConfig.getInitCapacity();
        if(capacity < 0){
            throw new IllegalArgumentException("capacity cannot be less than 0");
        }
        if(Objects.isNull(factory)){
            throw new NullPointerException();
        }
        this.poolConfig = poolConfig;
        objectArr = new Object[capacity];
        objectStatus = new int[capacity];
        init();
    }

    public ArrayObjectPool(PooledObjectFactory<T> factory) throws Exception {
        this(ObjectPoolConfig.getDefault(),factory);
    }

    @Override
    public T poll() throws InterruptedException{
        T t = doGet();
        if(Objects.nonNull(t)){
            return t;
        }
        mainLock.lock();
        try{
            // keep waiting
            while(Objects.isNull(t)){
                notFull.wait();
                t = doGet();
            }
        }finally {
            mainLock.unlock();
        }
        return t;
    }
    @SuppressWarnings("unchecked")
    private T doGet(){
        for (int i = 0; i < objectStatus.length; i++) {
            if(objectStatus[i] == NOT_USE){
                synchronized(generateMemoryLockKey(i).intern()){
                    if(objectStatus[i] == NOT_USE){
                        objectStatus[i] = USE;
                        return (T) objectArr[i];
                    }
                }
            }
        }
        return null;
    }

    @Override
    public T poll(long time, TimeUnit timeUnit) throws InterruptedException {
        T t = doGet();
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
                remaining = notFull.awaitNanos(timeUnit.toNanos(remaining));
                if(remaining <= 0L){
                    return null;
                }
                t = doGet();
            }
        }finally {
            mainLock.unlock();
        }
        return t;
    }

    @Override
    public T get() {
        boolean isFull = IntStream.of(objectStatus).allMatch(status -> status == USE);
        if(isFull){
            return null;
        }
        return doGet();
    }

    @Override
    public void back(T object) {
        if(Objects.isNull(object)){
            throw new NullPointerException();
        }
        for (int i = 0; i < objectArr.length; i++) {
            if(objectArr[i] == object){
                objectStatus[i] = NOT_USE;
                break;
            }
        }
        mainLock.lock();
        try{
            notFull.notify();
        }finally {
            mainLock.unlock();
        }
    }

    private String generateMemoryLockKey(int index){
        return String.format(LOCK_KEY_FORMAT,index);
    }

    @Override
    public void addObject() throws Exception {
        PooledObject<T> pooledObject = factory.makeObject();
        validateObject(pooledObject);
        T realObject = pooledObject.getObject();
        mainLock.lock();
        try{
            int length = objectArr.length;
            if(length == poolConfig.getInitCapacity()){
                throw new RuntimeException("Pool capacity is full and cannot continue to be added");
            }
            objectArr[currentIndex] = realObject;
            objectStatus[currentIndex] = NOT_USE;
            currentIndex++;
        }finally {
            mainLock.unlock();
        }
    }

    @Override
    public void validateObject(PooledObject<T> pooledObject) throws Exception {
        if(Objects.isNull(pooledObject)){
            throw new NullPointerException();
        }
        T realObject = pooledObject.getObject();
        if(Objects.isNull(realObject)){
            throw new NullPointerException();
        }
    }

    @Override
    public PooledObject<?> remove(PooledObject<?> object) throws Exception {
        mainLock.lock();
        try{
           return (PooledObject<?>) Stream.of(objectArr).filter(item -> item == object).findFirst().orElse(null);
        }finally {
            mainLock.unlock();
        }
    }

    /**
     * 初始化 object
     * @throws Exception 创建失败
     */
    private void init() throws Exception {
        int minIdle = poolConfig.getMinIdle();
        if(minIdle < 0){
            throw new IllegalArgumentException("minIdle cannot be less than 0");
        }
        for (int i = 0; i < minIdle; i++) {
            addObjects(minIdle);
        }
    }
}
