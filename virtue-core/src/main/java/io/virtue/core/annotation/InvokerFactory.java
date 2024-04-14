package io.virtue.core.annotation;

import io.virtue.common.spi.Extension;

import java.lang.annotation.*;

/**
 * Create the Invoker implementation of the extension protocol.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface InvokerFactory {

    /**
     * {@link io.virtue.core.InvokerFactory}‘s {@link Extension#value()}.
     *
     * @return {@link Extension#value()}
     */
    String value();

}
