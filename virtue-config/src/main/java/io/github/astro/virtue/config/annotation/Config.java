package io.github.astro.virtue.config.annotation;

import io.github.astro.virtue.common.constant.Components;

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
     * The group.
     */
    String group() default "";

    /**
     * The version.
     */
    String version() default "";

    /**
     * Serialization type.
     */
    String serialize() default Serialize.KRYO;

    /**
     * Invoke Filter.
     * @see io.github.astro.virtue.config.filter.Filter
     */
    String[] filters() default {"jjj"};

    /**
     * The filter chain for {@link io.github.astro.virtue.config.Caller}
     */
    String filterChain() default Components.DEFAULT;
}

