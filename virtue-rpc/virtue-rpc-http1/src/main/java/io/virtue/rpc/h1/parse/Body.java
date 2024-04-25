package io.virtue.rpc.h1.parse;

import java.lang.annotation.*;

/**
 * Http body.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Body {
}
