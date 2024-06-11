package io.virtue.rpc.registry;

import com.sun.management.OperatingSystemMXBean;
import io.virtue.common.extension.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.core.RemoteService;
import io.virtue.core.Virtue;
import io.virtue.core.manager.RemoteServiceManager;
import io.virtue.rpc.protocol.Protocol;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.Map;

/**
 * SystemInfo.
 */
@Data
@ToString
@Accessors(fluent = true)
public class DefaultRegistryMetaData {

    private static final OperatingSystemMXBean OS_BEAN = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    private double cpuUsage;

    private double memoryUsage;

    private long connections;

    private int services;

    private double loadAverage;

    public DefaultRegistryMetaData() {

    }

    public DefaultRegistryMetaData(URL url) {
        Virtue virtue = Virtue.ofLocal(url);
        // Get the server CPU usage
        cpuUsage = round(OS_BEAN.getCpuLoad());
        loadAverage = round(OS_BEAN.getSystemLoadAverage());
        // Get the server memory usage
        long totalMemory = OS_BEAN.getTotalMemorySize();
        long freeMemory = OS_BEAN.getFreeMemorySize();
        long usedMemory = totalMemory - freeMemory;
        memoryUsage = round((double) usedMemory / totalMemory);
        RemoteServiceManager remoteServiceManager = virtue.configManager().remoteServiceManager();
        Collection<RemoteService<?>> remoteServices = remoteServiceManager.remoteServices();
        services = remoteServices.stream()
                .mapToInt(remoteService -> remoteService.getInvokers(url.protocol()).length)
                .sum();
        Protocol protocol = ExtensionLoader.loadExtension(Protocol.class, url.protocol());
        connections = protocol.endpoints().servers().stream().mapToInt(server -> server.channels().length).sum();
    }

    /**
     * Convert map to DefaultRegistryMetaData.
     *
     * @param map
     * @return
     */
    public static DefaultRegistryMetaData valueOf(Map<String, String> map) {
        DefaultRegistryMetaData defaultRegistryMetaData = new DefaultRegistryMetaData();
        defaultRegistryMetaData.cpuUsage(Double.parseDouble(map.get("cpuUsage")));
        defaultRegistryMetaData.memoryUsage(Double.parseDouble(map.get("memoryUsage")));
        defaultRegistryMetaData.connections(Long.parseLong(map.get("connections")));
        defaultRegistryMetaData.services(Integer.parseInt(map.get("services")));
        defaultRegistryMetaData.loadAverage(Double.parseDouble(map.get("loadAverage")));
        return defaultRegistryMetaData;
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    /**
     * SystemInfo to map.
     *
     * @return
     */
    public Map<String, String> toMap() {
        return Map.of(
                "cpuUsage", String.valueOf(cpuUsage),
                "memoryUsage", String.valueOf(memoryUsage),
                "connections", String.valueOf(connections),
                "services", String.valueOf(services),
                "loadAverage", String.valueOf(loadAverage)
        );
    }
}
