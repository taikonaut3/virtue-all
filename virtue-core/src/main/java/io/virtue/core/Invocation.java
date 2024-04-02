package io.virtue.core;

import io.virtue.common.url.URL;

import java.lang.reflect.Type;
import java.util.function.Supplier;

/**
 * Encapsulate the url, callArgs, and invocation behavior.
 */
public interface Invocation {

    /**
     * Get the url of the invocation.
     *
     * @return current Invocation url
     */
    URL url();

    /**
     * Get the arguments of the method call.
     *
     * @return
     */
    Object[] args();

    /**
     * Get the return type of the method.
     *
     * @return {@link Invoker#returnType()}
     */
    Type returnType();

    /**
     * Get the parameter types of the method.
     *
     * @return {@link Invoker#method()}
     */
    Type[] parameterTypes();

    /**
     * Get the caller object for the method.
     *
     * @return
     */
    Invoker<?> invoker();

    /**
     * Invoke the invocation and return the result.
     *
     * @return invocation behavior (could be null)
     */
    Object invoke();

    /**
     * Revise current invocation behavior.
     *
     * @param invoke
     * @return
     */
    Invocation revise(Supplier<Object> invoke);

}
