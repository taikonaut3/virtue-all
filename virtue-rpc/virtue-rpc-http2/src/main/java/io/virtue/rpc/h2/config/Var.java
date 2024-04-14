package io.virtue.rpc.h2.config;

import java.lang.annotation.*;

/**
 * Http Config Variable.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Var {

    /**
     * Variable name.
     *
     * @return
     */
    String value();
}
