package io.github.taikonaut3.virtue.rpc.objectfactory;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * @author Chang Liu
 */
@SuppressWarnings("unchecked")
public class ArrayObjectPool<T> extends AbstractObjectPool<T>{

    private static final Logger LOGGER = LoggerFactory.getLogger(ArrayObjectPool.class);
    private final PooledObject<T>[] pooledObjectArr;
    private final ReentrantLock mainLock = new ReentrantLock();
    private final Condition notEmpty = mainLock.newCondition();
    private static final String LOCK_KEY_FORMAT = "pool-lock-%d";

    private final ObjectPoolConfig poolConfig;

    private int size;

    public ArrayObjectPool(ObjectPoolConfig poolConfig,PooledObjectFactory<T> factory){
        super(factory);
        int capacity = poolConfig.getInitCapacity();
        if(capacity < 0){
            throw new IllegalArgumentException("capacity cannot be less than 0");
        }
        if(Objects.isNull(factory)){
            throw new NullPointerException();
        }
        this.poolConfig = poolConfig;
        pooledObjectArr = new PooledObject[capacity];
        init();
    }

    public ArrayObjectPool(PooledObjectFactory<T> factory){
        this(ObjectPoolConfig.getDefault(),factory);
    }

    @Override
    public PooledObject<T> poll() throws InterruptedException{
        PooledObject<T> t = get();
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
    private PooledObject<T> doGet(){
        for (int i = 0; i < size; i++) {
            if(pooledObjectArr[i].getState() == PooledObjectState.IDLE){
                synchronized(generateMemoryLockKey(i).intern()){
                    if(pooledObjectArr[i].getState() == PooledObjectState.IDLE){
                        pooledObjectArr[i].setState(PooledObjectState.ALLOCATED);
                        return pooledObjectArr[i];
                    }
                }
            }
        }
        return null;
    }

    @Override
    public PooledObject<T> poll(long time, TimeUnit timeUnit) throws InterruptedException {
        PooledObject<T> t = get();
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
    @SneakyThrows
    public PooledObject<T> get() {
        boolean isFull = Arrays.stream(pooledObjectArr).allMatch(pooledObject -> pooledObject.getState() == PooledObjectState.ALLOCATED);
        if(isFull && size == poolConfig.getInitCapacity()){
            return null;
        }
        addObject();
        return doGet();
    }

    @Override
    public void back(PooledObject<T> pooledObject) throws Exception {
        validateObject(pooledObject);
        for (int i = 0; i < size; i++) {
            if(pooledObjectArr[i] == pooledObject){
                pooledObjectArr[i].setState(PooledObjectState.IDLE);
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
    public void addObject() throws Exception {
        PooledObject<T> pooledObject = factory.makeObject();
        validateObject(pooledObject);
        mainLock.lock();
        try{
            if(size == poolConfig.getInitCapacity()){
                throw new RuntimeException("Pool capacity is full and cannot continue to be added");
            }
            pooledObjectArr[size] = pooledObject;
            pooledObject.setState(PooledObjectState.IDLE);
            notEmpty.notify();
            size++;
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
    public boolean remove(PooledObject<T> pooledObject) throws Exception {
        validateObject(pooledObject);
        mainLock.lock();
        try{
            for (int i = 0; i < size; i++) {
                if(pooledObjectArr[i] == pooledObject){
                    fastRemove(pooledObjectArr,i,() -> null);
                    pooledObject.setState(PooledObjectState.INVALID);
                    return true;
                }
            }
        }finally {
            mainLock.unlock();
        }
        return false;
    }

    private void fastRemove(PooledObject<T>[] pooledObjectArr, int index, Supplier<PooledObject<T>> defaultValue){
       int newSize;
       // is not last element
       if((newSize = size - 1) > index){
           System.arraycopy(pooledObjectArr,index + 1,pooledObjectArr,index,newSize - index);
       }
        pooledObjectArr[size = newSize] = defaultValue.get();
    }
    @Override
    public int size() {
        return size;
    }

    /**
     * 初始化 object
     */
    private void init(){
        int minIdle = poolConfig.getMinIdle();
        int capacity = poolConfig.getInitCapacity();
        if(minIdle < 0){
            throw new IllegalArgumentException("minIdle cannot be less than 0");
        }
        if(minIdle > capacity){
            throw new IllegalArgumentException("minIdle cannot be more than capacity");
        }
        try {
            addObjects(minIdle);
        } catch (Exception e) {
            LOGGER.error("add object error {}",e.getMessage());
        }
    }
}
