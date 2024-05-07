package io.virtue.core.annotation;

import io.virtue.common.extension.spi.Extensible;
import io.virtue.common.extension.spi.Extension;

import java.lang.annotation.*;

/**
 * Create the Invoker implementation of the extension protocol.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Protocol {

    /**
     * {@link io.virtue.rpc.protocol.Protocol}‘s {@link Extensible#value()}.
     *
     * @return {@link Extension#value()}
     */
    String value();

}
