package objectpool;

import io.github.taikonaut3.virtue.config.manager.Virtue;
import io.github.taikonaut3.virtue.rpc.objectpool.ArrayObjectPool;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * ObjectPoolTest
 *
 * @Author WenBo Zhou
 * @Date 2024/2/27 16:05
 */
public class ObjectPoolTest {

    @Test
    public void test1() throws InterruptedException, IOException {
        Virtue virtue = Virtue.getDefault();
        ArrayObjectPool<SimpleObject> objectPool = new ArrayObjectPool<>(virtue, new SimpleObjectFactory(virtue));
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        executorService.execute(()->{
            for (int i = 0; i < 200; i++) {
                SimpleObject object = null;
                try {
                    object = objectPool.poll();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(object);
                objectPool.back(object);
            }
        });
        System.in.read();
    }
}
