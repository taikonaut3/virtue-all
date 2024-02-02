package io.github.astro.virtue.config.annotation;

import java.lang.annotation.*;

import static io.github.astro.virtue.common.constant.Components.Serialize;

/**
 * Common config for the client and the server.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Config {

    /**
     * The group
     */
    String group() default "";

    /**
     * The version
     */
    String version() default "";

    /**
     * Serialization type
     */
    String serialize() default Serialize.KRYO;

    /**
     * Invoke interceptors
     */
    String[] filters() default {};

    /**
     * The filter chain for {@link io.github.astro.virtue.config.Caller}
     */
    String filterChain() default "default";
}

