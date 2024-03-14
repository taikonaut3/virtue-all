package io.virtue.config.annotation;

import io.virtue.common.constant.Components;
import io.virtue.common.constant.Constant;

import java.lang.annotation.*;

/**
 * Client parameter configuration.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Options {

    /**
     * The default is false,Unit: ms
     */
    int timeout() default Constant.DEFAULT_TIMEOUT;

    /**
     * Used {@link io.virtue.governance.directory.Directory}.
     * Default is "default" {@link io.virtue.governance.directory.DefaultDirectory}.
     */
    String directory() default Components.DEFAULT;

    /**
     * Used {@link io.virtue.governance.router.Router}.
     * Default is "weight" {@link io.virtue.governance.router.DefaultRouter}.
     */
    String router() default Components.DEFAULT;

    /**
     * Used {@link io.virtue.governance.loadbalance.LoadBalance}.
     * Default is "random" {@link io.virtue.governance.loadbalance.RandomLoadBalance}.
     */
    String loadBalance() default Components.LoadBalance.RANDOM;

    /**
     * Used {@link io.virtue.governance.faulttolerance.FaultTolerance}.
     * Default is "failFast" {@link io.virtue.governance.faulttolerance.FailFast}.
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

}

