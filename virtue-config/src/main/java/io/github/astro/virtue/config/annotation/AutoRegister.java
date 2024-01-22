package io.github.astro.virtue.config.annotation;

import io.github.astro.virtue.config.ClientCaller;
import io.github.astro.virtue.config.ServerCaller;

import java.lang.annotation.*;

/**
 * Config the Caller implementation of the extension protocol
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoRegister {

    /**
     * The protocol
     */
    String protocol();

    /**
     * The Caller implementation of the Client
     */
    Class<? extends ClientCaller> clientCaller() default ClientCaller.class;

    /**
     * The Caller implementation of the Server
     */
    Class<? extends ServerCaller> serverCaller() default ServerCaller.class;
}
