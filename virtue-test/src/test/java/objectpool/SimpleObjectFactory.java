package objectpool;

import io.virtue.core.Virtue;
import objectpool.objectpool.DefaultPooledObject;
import objectpool.objectpool.PooledObject;
import objectpool.objectpool.PooledObjectFactory;

/**
 * @Author WenBo Zhou
 * @Date 2024/2/27 16:35
 */
public class SimpleObjectFactory implements PooledObjectFactory<SimpleObject> {

    private final Virtue virtue;

    public SimpleObjectFactory(Virtue virtue) {
        this.virtue = virtue;
    }

    @Override
    public PooledObject<SimpleObject> makeObject() {
        DefaultPooledObject<SimpleObject> pooledObject = new DefaultPooledObject<>(new SimpleObject());
        pooledObject.virtue(virtue);
        return pooledObject;
    }

    @Override
    public void destroyObject(PooledObject<SimpleObject> object) {

    }
}
