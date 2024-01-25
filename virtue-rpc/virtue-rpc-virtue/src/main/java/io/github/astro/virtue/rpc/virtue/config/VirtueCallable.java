package io.github.astro.virtue.rpc.virtue.config;

import io.github.astro.virtue.rpc.virtue.VirtueServerCaller;
import io.github.astro.virtue.config.annotation.BindingCaller;
import io.github.astro.virtue.config.annotation.Config;

import java.lang.annotation.*;

import static io.github.astro.virtue.common.constant.Components.Protocol.VIRTUE;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BindingCaller(protocol = VIRTUE, serverCaller = VirtueServerCaller.class)
public @interface VirtueCallable {

    String name() default "";

    String desc() default "";

    Config config() default @Config;
}
