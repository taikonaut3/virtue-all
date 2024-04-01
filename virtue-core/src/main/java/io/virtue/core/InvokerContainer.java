package io.virtue.core;

import java.lang.reflect.Method;

/**
 * The Caller Container For managing the caller.
 */
public interface InvokerContainer extends Lifecycle {

    /**
     * Gets the name of the microservice application used to locate the target IP.
     *
     * @return application-name
     */
    String remoteApplication();

    /**
     * Gets an array of callers associated with the container.
     *
     * @return all caller instance
     */
    Invoker<?>[] invokers();

    /**
     * Gets the caller associated with the specified method.
     *
     * @param method the method for which to get the associated invoker
     * @return the caller associated with the method, or null if not found
     */
    Invoker<?> getInvoker(Method method);

    /**
     * The Caller container Proxy type.
     *
     * @return proxy type
     */
    String proxy();

    /**
     * The belong to virtue.
     *
     * @return virtue instance
     */
    Virtue virtue();

}

