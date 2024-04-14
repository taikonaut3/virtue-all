package io.virtue.rpc.virtue.config;

import io.virtue.core.annotation.InvokerFactory;

import java.lang.annotation.*;

import static io.virtue.common.constant.Components.Protocol.VIRTUE;

/**
 * Support be Virtue protocol call.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@InvokerFactory(VIRTUE)
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
