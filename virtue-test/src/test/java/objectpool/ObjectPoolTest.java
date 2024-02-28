package objectpool;

import io.github.taikonaut3.virtue.config.manager.Virtue;
import io.github.taikonaut3.virtue.rpc.objectpool.ArrayObjectPool;
import org.junit.jupiter.api.Test;

/**
 * ObjectPoolTest
 *
 * @Author WenBo Zhou
 * @Date 2024/2/27 16:05
 */
public class ObjectPoolTest {



    @Test
    public void test1() throws InterruptedException {
        Virtue virtue = Virtue.getDefault();
        ArrayObjectPool<SimpleObject> objectPool = new ArrayObjectPool<>(virtue, new SimpleObjectFactory(virtue));
        SimpleObject object = objectPool.poll();
        System.out.println(object);
    }
}
