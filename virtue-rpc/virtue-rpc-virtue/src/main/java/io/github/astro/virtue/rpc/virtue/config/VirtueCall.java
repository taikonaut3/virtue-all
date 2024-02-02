package io.github.astro.virtue.rpc.virtue.config;

import io.github.astro.virtue.config.annotation.Config;
import io.github.astro.virtue.config.annotation.Options;
import io.github.astro.virtue.config.annotation.RegistryCallerFactory;
import io.github.astro.virtue.rpc.virtue.VirtueCallerFactory;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RegistryCallerFactory(VirtueCallerFactory.class)
public @interface VirtueCall {

    String service() default "";

    String callMethod() default "";

    Options options() default @Options;

    Config config() default @Config;
}
