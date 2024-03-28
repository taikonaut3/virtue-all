package io.virtue.core.config;

import io.virtue.common.constant.Constant;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Application Config.
 */
@Data
@Accessors(fluent = true, chain = true)
public class ApplicationConfig {

    private String name;

    private int weight = Constant.DEFAULT_WEIGHT;

    private String transport = Constant.DEFAULT_TRANSPORTER;

    private String router = Constant.DEFAULT_ROUTER;

    private EventDispatcherConfig eventDispatcherConfig = new EventDispatcherConfig();

    public ApplicationConfig() {

    }

    public ApplicationConfig(String name) {
        this.name = name;
    }

}
