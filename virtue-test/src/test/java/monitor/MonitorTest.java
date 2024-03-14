package monitor;

import io.virtue.config.SystemInfo;
import io.virtue.config.manager.Virtue;
import org.junit.jupiter.api.Test;

/**
 * @Author WenBo Zhou
 * @Date 2024/2/28 10:55
 */
public class MonitorTest {



    @Test
    public void test1(){
        Virtue virtue = Virtue.getDefault();
        SystemInfo systemInfo = new SystemInfo(virtue);
        System.out.println(systemInfo);
    }
}
