package io.github.astro.rpc.virtue.config;

import io.github.astro.rpc.virtue.VirtueClientCaller;
import io.github.astro.virtue.config.annotation.AutoRegister;
import io.github.astro.virtue.config.annotation.Config;
import io.github.astro.virtue.config.annotation.Options;

import java.lang.annotation.*;

import static io.github.astro.virtue.common.constant.Components.Protocol.VIRTUE;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AutoRegister(protocol = VIRTUE, clientCaller = VirtueClientCaller.class)
public @interface VirtueCall {

    String service() default "";

    String callMethod() default "";

    Options options() default @Options;

    Config config() default @Config;
}
