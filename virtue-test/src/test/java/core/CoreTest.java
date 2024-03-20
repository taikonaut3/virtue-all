package core;

import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.core.config.EventDispatcherConfig;
import io.virtue.event.EventDispatcher;
import org.junit.jupiter.api.Test;

/**
 * @Author WenBo Zhou
 * @Date 2024/3/19 16:14
 */
public class CoreTest {

    @Test
    public void test1() {
        URL url = new EventDispatcherConfig().toUrl();
        EventDispatcher eventDispatcher = ExtensionLoader.load(EventDispatcher.class).conditionOnConstructor(url).getService("disruptor");
        System.out.println(eventDispatcher);
    }
}
