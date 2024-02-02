package io.github.astro.virtue.rpc.virtue.config;

import io.github.astro.virtue.config.annotation.Config;
import io.github.astro.virtue.config.annotation.RegistryCallerFactory;
import io.github.astro.virtue.rpc.virtue.VirtueCallerFactory;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RegistryCallerFactory(VirtueCallerFactory.class)
public @interface VirtueCallable {

    String name() default "";

    String desc() default "";

    Config config() default @Config;
}
