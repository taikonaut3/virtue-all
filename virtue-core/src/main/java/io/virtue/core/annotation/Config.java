package io.virtue.core.annotation;

import io.virtue.common.constant.Components;
import io.virtue.common.constant.Constant;
import io.virtue.core.Invoker;
import io.virtue.core.filter.Filter;

import java.lang.annotation.*;

/**
 * Common config for the client and the server.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Config {

    /**
     * Serialization type.
     */
    String serialization() default Constant.DEFAULT_SERIALIZATION;

    /**
     * Compression type.
     */
    String compression() default Constant.DEFAULT_COMPRESSION;

    /**
     * The filter chain for {@link Invoker}.
     */
    String filterChain() default Components.DEFAULT;

    /**
     * Invoke Filters.
     *
     * @see Filter
     */
    String[] filters() default {};
}

