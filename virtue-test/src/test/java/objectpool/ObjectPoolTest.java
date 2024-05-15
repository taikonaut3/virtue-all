package objectpool;

import io.virtue.core.Virtue;
import objectpool.objectpool.ArrayObjectPool;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
        ExecutorService executorService = Executors.newFixedThreadPool(200);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int j = 0; j < 200; j++) {
            executorService.execute(() -> {
                SimpleObject object = null;
                try {
                    object = objectPool.poll();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println(object + "-" + atomicInteger.getAndIncrement());
                    objectPool.back(object);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        System.in.read();
    }
}
