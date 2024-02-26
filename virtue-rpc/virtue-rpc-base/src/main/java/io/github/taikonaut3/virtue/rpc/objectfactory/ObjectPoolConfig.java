package io.github.taikonaut3.virtue.rpc.objectfactory;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Chang Liu
 */
@Setter
@Getter
@Accessors(fluent = true)
public class ObjectPoolConfig {
    private int initCapacity;
    private int minIdle;
    private int maxIdle;

    public static ObjectPoolConfig getDefault(){
        ObjectPoolConfig defaultConfig = new ObjectPoolConfig();
        defaultConfig.initCapacity(100);
        defaultConfig.minIdle(10);
        defaultConfig.maxIdle(10);
        return defaultConfig;
    }
}
