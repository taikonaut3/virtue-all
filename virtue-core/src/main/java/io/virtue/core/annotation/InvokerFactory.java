package io.virtue.core.annotation;

import io.virtue.common.spi.ServiceProvider;

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
     * {@link io.virtue.core.InvokerFactory}‘s {@link ServiceProvider#value()}.
     *
     * @return {@link ServiceProvider#value()}
     */
    String value();


}
