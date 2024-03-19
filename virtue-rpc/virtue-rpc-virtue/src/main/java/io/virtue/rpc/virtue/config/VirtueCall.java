package io.virtue.rpc.virtue.config;

import io.virtue.core.annotation.CallerFactoryProvider;
import io.virtue.core.annotation.Config;
import io.virtue.core.annotation.Options;
import io.virtue.common.constant.Components;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@CallerFactoryProvider(Components.Protocol.VIRTUE)
public @interface VirtueCall {

    String service() default "";

    String callMethod() default "";

    Options options() default @Options;

    Config config() default @Config;
}
