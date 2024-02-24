package io.github.taikonaut3.virtue.rpc.objectfactory;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Chang Liu
 */
@Setter
@Getter
public class ObjectPoolConfig {
    private int initCapacity;
    private int minIdle;
    private int maxIdle;

    public static ObjectPoolConfig getDefault(){
        ObjectPoolConfig defaultConfig = new ObjectPoolConfig();
        defaultConfig.setInitCapacity(100);
        defaultConfig.setMinIdle(10);
        defaultConfig.setMaxIdle(10);
        return defaultConfig;
    }
}
