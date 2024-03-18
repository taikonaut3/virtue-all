package io.virtue.config.annotation;

import io.virtue.common.constant.Constant;
import io.virtue.config.ClientCaller;

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
     * The remoteApplication name or URL
     */
    String value();

    /**
     * The proxy type
     */
    String proxy() default Constant.DEFAULT_PROXY;

    /**
     * Is it only when the first call is made that the registration center is actually connected to Get the available services.
     * The default gets available services when {@link ClientCaller} creation is complete.
     */
    boolean lazyDiscover() default false;

}
