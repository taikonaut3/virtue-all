package monitor;

import io.virtue.core.SystemInfo;
import io.virtue.core.Virtue;
import org.junit.jupiter.api.Test;

/**
 * @Author WenBo Zhou
 * @Date 2024/2/28 10:55
 */
public class MonitorTest {

    @Test
    public void test1() {
        Virtue virtue = Virtue.getDefault();
        SystemInfo systemInfo = new SystemInfo(virtue);
        System.out.println(systemInfo);
    }
}
