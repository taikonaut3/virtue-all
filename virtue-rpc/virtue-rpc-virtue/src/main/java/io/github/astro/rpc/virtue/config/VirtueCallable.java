package io.github.astro.rpc.virtue.config;

import io.github.astro.rpc.virtue.VirtueServerCaller;
import io.github.astro.virtue.config.annotation.AutoRegister;
import io.github.astro.virtue.config.annotation.Config;

import java.lang.annotation.*;

import static io.github.astro.virtue.common.constant.Components.Protocol.VIRTUE;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AutoRegister(protocol = VIRTUE, serverCaller = VirtueServerCaller.class)
public @interface VirtueCallable {

    String name() default "";

    String desc() default "";

    Config config() default @Config;
}
