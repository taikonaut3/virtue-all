package io.virtue.core.annotation;

import io.virtue.common.constant.Constant;

import java.lang.annotation.*;

/**
 * Client invoke parameter configuration.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Options {

    /**
     * The group.
     */
    String group() default Constant.DEFAULT_GROUP;

    /**
     * The default is false,Unit: ms.
     */
    int timeout() default Constant.DEFAULT_TIMEOUT;

    /**
     * Used {@link io.virtue.governance.discovery.ServiceDiscovery}.
     * Default is "default" {@link io.virtue.governance.discovery.ServiceDiscovery}.
     */
    String serviceDiscovery() default Constant.DEFAULT_SERVICE_DISCOVERY;

    /**
     * Used {@link io.virtue.governance.router.Router}.
     * Default is "default" {@link io.virtue.governance.router.DefaultRouter}.
     */
    String router() default Constant.DEFAULT_ROUTER;

    /**
     * Used {@link io.virtue.governance.loadbalance.LoadBalance}.
     * Default is "random" {@link io.virtue.governance.loadbalance.RandomLoadBalance}.
     */
    String loadBalance() default Constant.DEFAULT_LOAD_BALANCE;

    /**
     * Used {@link io.virtue.governance.faulttolerance.FaultTolerance}.
     * Default is "failFast" {@link io.virtue.governance.faulttolerance.FailFast}.
     */
    String faultTolerance() default Constant.DEFAULT_FAULT_TOLERANCE;

    /**
     * If {@link Options#faultTolerance()} is "failRetry"{@link io.virtue.governance.faulttolerance.FailRetry},
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

