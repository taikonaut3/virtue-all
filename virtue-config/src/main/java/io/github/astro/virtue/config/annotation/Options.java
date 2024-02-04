package io.github.astro.virtue.config.annotation;

import io.github.astro.virtue.common.constant.Components;
import io.github.astro.virtue.common.constant.Constant;

import java.lang.annotation.*;

/**
 * Client parameter configuration.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Options {

    /**
     * Supports async call,The default is false.
     * If true,Then the returnType should be {@link java.util.concurrent.CompletableFuture}.
     */
    boolean async() default false;

    /**
     * The default is false,Unit: ms
     */
    int timeout() default Constant.DEFAULT_TIMEOUT;

    /**
     * Used {@link io.github.astro.virtue.governance.directory.Directory}.
     * Default is "default" {@link io.github.astro.virtue.governance.directory.DefaultDirectory}.
     */
    String directory() default Components.DEFAULT;

    /**
     * Used {@link io.github.astro.virtue.governance.router.Router}.
     * Default is "weight" {@link io.github.astro.virtue.governance.router.WeightRouter}.
     */
    String router() default Components.Router.WEIGHT;

    /**
     * Used {@link io.github.astro.virtue.governance.loadbalance.LoadBalance}.
     * Default is "random" {@link io.github.astro.virtue.governance.loadbalance.RandomLoadBalance}.
     */
    String loadBalance() default Components.LoadBalance.RANDOM;

    /**
     * Used {@link io.github.astro.virtue.governance.faulttolerance.FaultTolerance}.
     * Default is "failFast" {@link io.github.astro.virtue.governance.faulttolerance.FailFast}.
     */
    String faultTolerance() default Components.FaultTolerance.FAIL_FAST;

    /**
     * If {@link Options#faultTolerance()} is "failRetry",
     * Number of retries called when an exception occurred.
     */
    int retires() default Constant.DEFAULT_RETIRES;

    /**
     * Precise direct connection to the url address.
     */
    String url() default "";

    /**
     * Determines whether to reuse the client configuration from the current protocol.
     */
    boolean multiplex() default true;

    /**
     * When {@link Options#multiplex()} is false,
     * Capable to configure custom clients,Then a client will be created.
     */
    String client() default "";

    /**
     * Supports multiple registry configs.
     */
    String[] registries() default {};

    /**
     * Is it only when the first call is made that the registration center is actually connected to Get the available services.
     * The default gets available services when {@link io.github.astro.virtue.config.ClientCaller} creation is complete.
     */
    boolean lazyDiscover() default false;

}

