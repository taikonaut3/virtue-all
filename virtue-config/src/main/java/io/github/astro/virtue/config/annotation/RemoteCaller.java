package io.github.astro.virtue.config.annotation;

import io.github.astro.virtue.common.constant.Components;

import java.lang.annotation.*;

/**
 * Client representing a service
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RemoteCaller {

    /**
     * The RemoteApplication name
     */
    String value();

    /**
     * The Proxy type
     */
    String proxy() default Components.ProxyFactory.JDK;

}
