package io.github.astro.virtue.config.annotation;

import io.github.astro.virtue.common.spi.ServiceProvider;

import java.lang.annotation.*;

/**
 * Create the Caller implementation of the extension protocol
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CallerFactoryProvider {

    /**
     * CallerFactory‘s {@link ServiceProvider#value()}
     *
     * @return class object
     */
    String value();


}
