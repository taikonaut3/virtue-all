package io.virtue.rpc.h2.config;

import java.lang.annotation.*;

/**
 * Http body.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Body {
}
