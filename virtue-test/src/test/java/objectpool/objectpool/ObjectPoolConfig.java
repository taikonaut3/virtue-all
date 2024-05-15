package objectpool.objectpool;

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
    private int initCapacity = 100;
    private int minIdle = 10;
    private int maxIdle = 10;
}
