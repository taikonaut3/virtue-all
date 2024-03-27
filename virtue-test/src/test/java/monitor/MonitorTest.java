package monitor;

import io.virtue.core.SystemInfo;
import io.virtue.core.Virtue;
import org.junit.jupiter.api.Test;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;

import java.util.List;

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

    @Test
    public void test2(){
        oshi.SystemInfo systemInfo = new oshi.SystemInfo();
        OperatingSystem system = systemInfo.getOperatingSystem();
        List<NetworkIF> networkIFs = systemInfo.getHardware().getNetworkIFs();
        for (NetworkIF net : networkIFs) {
            if(net.getIfOperStatus()== NetworkIF.IfOperStatus.UP){
                System.out.println("接收的包:"+net.getPacketsRecv()+",发送的包:"+net.getPacketsSent());
            }
        }
    }
}
