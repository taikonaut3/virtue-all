package io.virtue.config.annotation;

import io.virtue.common.constant.Components;
import io.virtue.config.Caller;
import io.virtue.config.filter.Filter;

import java.lang.annotation.*;

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
    String serialize() default Components.Serialize.KRYO;

    /**
     * Compression type
     */
    String compression() default Components.Compression.GZIP;

    /**
     * Invoke Filter.
     * @see Filter
     */
    String[] filters() default {};

    /**
     * The filter chain for {@link Caller}
     */
    String filterChain() default Components.DEFAULT;
}

