package io.virtue.core;

import io.virtue.common.exception.RpcException;
import io.virtue.common.url.URL;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Method invoker wrapper for the client method and server method.
 *
 * @param <T> Method Annotation
 */
public interface Invoker<T extends Annotation> extends Config, Lifecycle {

    /**
     * The associated annotation instance.
     *
     * @return
     */
    T parsedAnnotation();

    /**
     * The protocol.
     *
     * @return
     */
    String protocol();

    /**
     * Invoke url core.
     *
     * @return
     */
    URL url();

    /**
     * All proxy clients invoke the entry.
     * @param invocation
     * @return
     * @throws RpcException
     */
    Object invoke(Invocation invocation) throws RpcException;

    /**
     * The client interface invoke method.
     *
     * @return
     */
    Method method();

    /**
     * The client interface returnType.
     *
     * @return
     */
    Type returnType();

    /**
     * The client interface returnClass.
     *
     * @return
     */
    Class<?> returnClass();

    /**
     * Container holding the invoker.
     *
     * @return
     */
    InvokerContainer container();

    /**
     * Gets the path as list associated with the server callee.
     *
     * @return
     */
    List<String> pathList();

    /**
     * Gets the path associated with the server callee.
     *
     * @return
     */
    default String path() {
        return URL.toPath(pathList());
    }

    /**
     * The belong to virtue.
     *
     * @return
     */
    default Virtue virtue() {
        return container().virtue();
    }

}
