package io.github.astro.virtue.config.annotation;

import io.github.astro.virtue.config.CallerFactory;

import java.lang.annotation.*;

/**
 * Config the Caller implementation of the extension protocol
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RegistryCallerFactory {

    Class<? extends CallerFactory> value();


}
