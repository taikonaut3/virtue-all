package io.github.astro.virtue.config.annotation;

import io.github.astro.virtue.common.constant.Components;

import java.lang.annotation.*;

/**
 * Indicates a remote caller,
 * Used on interface, remoteCaller can create a proxy for that interface to perform remote invocations.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RemoteCaller {

    /**
     * The remoteApplication name
     */
    String value();

    /**
     * The proxy type
     */
    String proxy() default Components.ProxyFactory.JDK;

}
