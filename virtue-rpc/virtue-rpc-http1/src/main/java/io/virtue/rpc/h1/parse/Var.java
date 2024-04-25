package io.virtue.rpc.h1.parse;

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
