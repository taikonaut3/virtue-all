package io.virtue.core;

import io.virtue.common.exception.RpcException;
import io.virtue.common.url.URL;
import io.virtue.common.util.GenerateUtil;
import io.virtue.core.manager.Virtue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;


/**
 * Method caller wrapper for the client method and server method.
 * @param <T> Method Annotation
 */
public interface Caller<T extends Annotation> extends CommonConfig, Lifecycle {


    /**
     * The associated annotation instance.
     * @return
     */
    T parsedAnnotation();

    /**
     * The protocol.
     * @return
     */
    String protocol();

    /**
     * Call url core.
     * @return
     */
    URL url();

    /**
     * All proxy clients call the entry.
     * @param url
     * @param args
     * @return
     * @throws RpcException
     */
    Object call(URL url, CallArgs args) throws RpcException;

    /**
     * The client interface call method.
     * @return
     */
    Method method();

    /**
     * The client interface returnType.
     * @return
     */
    Type returnType();

    /**
     * The client interface returnClass.
     * @return
     */
    Class<?> returnClass();

    /**
     * Container holding the caller.
     * @return
     */
    CallerContainer container();

    /**
     * Gets the path as list associated with the server caller.
     * @return
     */
    List<String> pathList();

    /**
     * Gets the path associated with the server caller.
     * @return
     */
    default String path() {
        return URL.toPath(pathList());
    }

    /**
     * Caller unique identification.
     * @return
     */
    default String identification() {
        return GenerateUtil.generateCallerIdentification(protocol(), path());
    }

    /**
     * The belong to virtue.
     * @return
     */
    default Virtue virtue() {
        return container().virtue();
    }

}
