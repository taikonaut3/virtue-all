package io.virtue.config.config;

import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Components;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Setter
@Getter
public class ApplicationConfig {

    private String applicationName;

    private int weight = Constant.DEFAULT_WEIGHT;

    private String transport = Components.Transport.NETTY;

    private String router = Components.DEFAULT;

    private EventDispatcherConfig eventDispatcherConfig = new EventDispatcherConfig();

}
