package io.github.astro.virtue.config.annotation;

import io.github.astro.virtue.common.constant.Components;
import io.github.astro.virtue.common.constant.Constant;

import java.lang.annotation.*;

/**
 * Client-side parameter configuration.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Options {

    /**
     * Supports asynchronous invocation. Default value is false.
     */
    boolean async() default false;

    /**
     * Timeout duration in milliseconds. Default value is Constant.DEFAULT_TIMEOUT.
     */
    int timeout() default Constant.DEFAULT_TIMEOUT;

    /**
     * Number of retries. Default value is Constant.DEFAULT_RETIRES.
     */
    int retires() default Constant.DEFAULT_RETIRES;

    /**
     * Gets the service directory. Default value is Components.Directory.DEFAULT.
     */
    String directory() default Components.DEFAULT;

    /**
     * Gets the router type. Default value is Components.Router.WEIGHT.
     */
    String router() default Components.Router.WEIGHT;

    /**
     * Gets the load balancing strategy. Default value is Components.LoadBalance.RANDOM.
     */
    String loadBalance() default Components.LoadBalance.RANDOM;

    /**
     * Gets the fault tolerance strategy. Default value is Components.FaultTolerance.FAIL_RETRY.
     */
    String faultTolerance() default Components.FaultTolerance.FAIL_FAST;

    /**
     * Directly accesses the specified URL.
     */
    String url() default "";

    /**
     * Determines whether to reuse the client configuration from the current protocol. Default value is true.
     */
    boolean multiplex() default true;

    /**
     * When multiplex is set to false, a new client will be created using the configuration specified here.
     */
    String client() default "";

    /**
     * Supports multiple registry configs
     */
    String[] registries() default {};

    /**
     * Is it only when the first call is made that the registration center is actually
     * connected to Get the available services
     */
    boolean lazyDiscover() default false;

}

