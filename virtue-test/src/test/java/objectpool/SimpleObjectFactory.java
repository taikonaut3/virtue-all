package objectpool;

import io.github.taikonaut3.virtue.config.manager.Virtue;
import io.github.taikonaut3.virtue.rpc.objectpool.DefaultPooledObject;
import io.github.taikonaut3.virtue.rpc.objectpool.PooledObject;
import io.github.taikonaut3.virtue.rpc.objectpool.PooledObjectFactory;

/**
 * @Author WenBo Zhou
 * @Date 2024/2/27 16:35
 */
public class SimpleObjectFactory implements PooledObjectFactory<SimpleObject> {

    private Virtue virtue;

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
