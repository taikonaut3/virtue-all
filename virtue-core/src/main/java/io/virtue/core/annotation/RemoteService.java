package io.virtue.core.annotation;

import io.virtue.common.constant.Components;

import java.lang.annotation.*;

/**
 * Indicates a Remote service.
 * <p>Methods of the class with {@link RemoteService} can be called by the client.</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RemoteService {

    /**
     * The remoteService name.
     */
    String value();

    /**
     * The proxy type.
     */
    String proxy() default Components.ProxyFactory.CGLIB;

}
