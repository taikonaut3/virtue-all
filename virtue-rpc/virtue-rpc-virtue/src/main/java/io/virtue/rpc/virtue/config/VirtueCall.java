package io.virtue.rpc.virtue.config;

import io.virtue.core.annotation.InvokerFactory;
import io.virtue.core.annotation.Config;
import io.virtue.core.annotation.Options;
import io.virtue.common.constant.Components;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@InvokerFactory(Components.Protocol.VIRTUE)
public @interface VirtueCall {

    String service() default "";

    String callMethod() default "";

    Options options() default @Options;

    Config config() default @Config;
}
