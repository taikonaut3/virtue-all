package io.virtue.rpc.virtue.config;

import io.virtue.common.constant.Components;
import io.virtue.core.annotation.InvokerFactory;
import io.virtue.core.annotation.RemoteService;

import java.lang.annotation.*;

/**
 * Use Virtue protocol to make RPC call.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@InvokerFactory(Components.Protocol.VIRTUE)
public @interface VirtueCall {

    /**
     * Remote Service Name.
     *
     * @return
     * @see RemoteService#value()
     */
    String service() default "";

    /**
     * Remote Method.
     *
     * @return
     * @see VirtueCallable#name()
     */
    String callMethod() default "";

}
