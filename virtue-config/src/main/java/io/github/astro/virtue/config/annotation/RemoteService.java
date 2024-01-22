package io.github.astro.virtue.config.annotation;

import io.github.astro.virtue.common.constant.Components;

import java.lang.annotation.*;

/**
 * Indicates a remote service
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RemoteService {

    /**
     * The RemoteService Name
     */
    String value();

    /**
     * The Proxy type
     */
    String proxy() default Components.ProxyFactory.CGLIB;

}
