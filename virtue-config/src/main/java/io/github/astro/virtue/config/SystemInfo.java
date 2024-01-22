package io.github.astro.virtue.config;

import com.sun.management.OperatingSystemMXBean;
import io.github.astro.virtue.config.manager.RemoteServiceManager;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Map;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/13 22:12
 */

@Accessors(fluent = true)
@Data
public class SystemInfo {

    private double cpuUsage;

    private double memoryUsage;

    private long connections;

    private int services;

    private double loadAverage;

    public SystemInfo() {

    }

    public SystemInfo(long connections) {
        this.connections = connections;
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        // 获取服务器 CPU 使用率
        cpuUsage = Double.parseDouble(decimalFormat.format(osBean.getCpuLoad()));
        loadAverage = Double.parseDouble(decimalFormat.format(osBean.getSystemLoadAverage()));
        // 获取服务器内存使用情况
        long totalMemory = osBean.getTotalMemorySize();
        long freeMemory = osBean.getFreeMemorySize();
        long usedMemory = totalMemory - freeMemory;
        memoryUsage = Double.parseDouble(decimalFormat.format((double) usedMemory / totalMemory));
        RemoteServiceManager remoteServiceManager = Virtue.getDefault().configManager().remoteServiceManager();
        Collection<RemoteService<?>> remoteServices = remoteServiceManager.getRemoteService();
        services = remoteServices.stream()
                .mapToInt(remoteService -> remoteService.callers().length)
                .sum();
    }

    public static SystemInfo valueOf(Map<String, String> map) {
        SystemInfo systemInfo = new SystemInfo();
        systemInfo.cpuUsage(Double.parseDouble(map.get("cpuUsage")));
        systemInfo.memoryUsage(Double.parseDouble(map.get("memoryUsage")));
        systemInfo.connections(Long.parseLong(map.get("connections")));
        systemInfo.services(Integer.parseInt(map.get("services")));
        systemInfo.loadAverage(Double.parseDouble(map.get("loadAverage")));
        return systemInfo;
    }

    public Map<String, String> toMap() {
        return Map.of("cpuUsage", String.valueOf(cpuUsage)
                , "memoryUsage", String.valueOf(memoryUsage)
                , "connections", String.valueOf(connections)
                , "services", String.valueOf(services)
                , "loadAverage", String.valueOf(loadAverage));
    }
}
