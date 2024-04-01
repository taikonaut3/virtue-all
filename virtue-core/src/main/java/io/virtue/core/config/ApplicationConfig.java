package io.virtue.core.config;

import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.url.Parameter;
import io.virtue.common.url.Parameterization;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Application Config.
 */
@Data
@Accessors(fluent = true, chain = true)
public class ApplicationConfig implements Parameterization {

    private String name;

    @Parameter(Key.WEIGHT)
    private int weight = Constant.DEFAULT_WEIGHT;

    @Parameter(Key.GROUP)
    private String group = Constant.DEFAULT_GROUP;

    private String transport = Constant.DEFAULT_TRANSPORTER;

    private String router = Constant.DEFAULT_ROUTER;

    private EventDispatcherConfig eventDispatcherConfig = new EventDispatcherConfig();

    public ApplicationConfig() {

    }

    public ApplicationConfig(String name) {
        this.name = name;
    }

}
