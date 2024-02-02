package io.github.astro.virtue.config.annotation;

import io.github.astro.virtue.config.CallerFactory;

import java.lang.annotation.*;

/**
 * Create the Caller implementation of the extension protocol
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RegistryCallerFactory {

    /**
     * CallerFactory‘s Class
     *
     * @return class object
     */
    Class<? extends CallerFactory> value();


}
