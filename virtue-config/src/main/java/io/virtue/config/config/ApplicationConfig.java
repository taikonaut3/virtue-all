package io.virtue.config.config;

import io.virtue.common.constant.Constant;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Setter
@Getter
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
