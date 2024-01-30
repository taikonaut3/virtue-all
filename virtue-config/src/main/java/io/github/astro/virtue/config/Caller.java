package io.github.astro.virtue.config;

import io.github.astro.virtue.common.exception.RpcException;
import io.github.astro.virtue.common.url.URL;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Real caller for the client and server
 */
public interface Caller<T extends Annotation> extends CommonConfig, Lifecycle {

    /**
     * The associated annotation instance
     */
    T parsedAnnotation();

    /**
     * The protocol
     */
    String protocol();

    /**
     * Call url config
     */
    URL url();

    /**
     * All proxy clients call the entry
     *
     * @param args
     * @throws RpcException
     */
    Object call(CallArgs args) throws RpcException;

    /**
     * The client interface call method
     */
    Method method();

    /**
     * The client interface returnType
     */
    Type returnType();


    Class<?> returnClass();

    /**
     * Container holding the caller
     */
    CallerContainer container();

    /**
     * Local invoker
     */
    Invoker<?> invoker();

}
