package io.virtue.core.annotation;

import io.virtue.common.constant.Components;
import io.virtue.common.constant.Constant;
import io.virtue.core.Caller;
import io.virtue.core.filter.Filter;

import java.lang.annotation.*;

/**
 * Common core for the client and the server.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Config {

    /**
     * The group.
     */
    String group() default Constant.DEFAULT_GROUP;

    /**
     * The version.
     */
    String version() default Constant.DEFAULT_VERSION;

    /**
     * Serialization type.
     */
    String serialize() default Constant.DEFAULT_SERIALIZE;

    /**
     * Compression type
     */
    String compression() default Constant.DEFAULT_COMPRESSION;

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

