package io.github.taikonaut3.virtue.rpc.objectfactory;

/**
 * @author Chang Liu
 */
public interface PooledObjectFactory<T> {
    /**
     * 创建对象
     * @return t
     */
    PooledObject<T> makeObject();

    /**
     * 销毁对象
     * @param object 待销毁的对象
     */
    void destroyObject(PooledObject<T> object);
}
