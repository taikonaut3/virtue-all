package io.virtue.rpc.virtue.config;

import io.virtue.core.annotation.InvokerFactory;
import io.virtue.core.annotation.Config;
import io.virtue.common.constant.Components;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@InvokerFactory(Components.Protocol.VIRTUE)
public @interface VirtueCallable {

    String name() default "";

    String desc() default "";

    Config config() default @Config;
}
