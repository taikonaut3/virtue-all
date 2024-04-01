package io.virtue.core;

import java.lang.reflect.Method;

/**
 * Remote service.
 *
 * @param <T> The type of the remote service interface.
 */
public interface RemoteService<T> extends InvokerContainer {

    /**
     * Gets the target instance of the remote service.
     *
     * @return the target instance
     */
    T target();

    /**
     * Gets the name of the remote service.
     *
     * @return The name of the remote service
     */
    String name();

    /**
     * Invokes the specified method.
     * <p>It is not recommended to use it directly{@link Method#invoke(Object, Object...)}</p>
     *
     * @param method
     * @param args
     * @return
     */
    Object invokeMethod(Method method, Object[] args);

    /**
     * Gets the callee for the specified protocol and path.
     *
     * @param protocol The protocol used for the  callee
     * @param path     The path used for the callee
     * @return The callee
     */
    Callee<?> getCallee(String protocol, String path);

}

