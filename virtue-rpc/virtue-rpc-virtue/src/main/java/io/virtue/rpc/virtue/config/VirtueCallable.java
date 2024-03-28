package io.virtue.rpc.virtue.config;

import io.virtue.common.constant.Components;
import io.virtue.core.annotation.InvokerFactory;

import java.lang.annotation.*;

/**
 * Support be Virtue protocol call.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@InvokerFactory(Components.Protocol.VIRTUE)
public @interface VirtueCallable {

    /**
     * name of the method.
     *
     * @return
     */
    String name() default "";

    /**
     * desc of the method.
     *
     * @return
     */
    String desc() default "";

}
