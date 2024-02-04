package io.github.astro.virtue.config.annotation;

import io.github.astro.virtue.common.constant.Components;

import java.lang.annotation.*;

/**
 * Indicates a Remote service,
 * Methods of the class with remoteService can be called by the client.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RemoteService {

    /**
     * The remoteService name
     */
    String value();

    /**
     * The proxy type
     */
    String proxy() default Components.ProxyFactory.CGLIB;

}
