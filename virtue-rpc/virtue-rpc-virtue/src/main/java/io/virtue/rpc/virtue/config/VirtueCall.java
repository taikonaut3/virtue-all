package io.virtue.rpc.virtue.config;

import io.virtue.core.annotation.Protocol;
import io.virtue.core.annotation.RemoteService;

import java.lang.annotation.*;

import static io.virtue.common.constant.Components.Protocol.VIRTUE;

/**
 * Use Virtue protocol to make RPC call.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Protocol(VIRTUE)
public @interface VirtueCall {

    /**
     * Remote Service Name.
     *
     * @return
     * @see RemoteService#value()
     */
    String service();

    /**
     * Remote Method.
     *
     * @return
     * @see VirtueCallable#name()
     */
    String callMethod() default "";

}
