package core;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author WenBo Zhou
 * @Date 2024/3/19 16:14
 */
public class CoreTest {

    private final Map<String, String> map = new ConcurrentHashMap<>();

    @Test
    public void test1() {

    }

    @Test
    public void test2() {
        ExecutorService executorService = Executors.newFixedThreadPool(200);
        for (int i = 0; i < 1000; i++) {
            executorService.execute(() -> {
                System.out.println(get("111"));
            });
        }
    }

    private String get(String key) {
        String value = map.get(key);
        if (value == null) {
            synchronized (this) {
                if (map.get(key) == null) {
                    value = "111";
                    map.put(key, value);
                }
            }
        }
        return value;
    }
}
