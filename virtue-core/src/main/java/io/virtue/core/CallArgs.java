package io.virtue.core;

import java.lang.reflect.Type;

/**
 * Encapsulate the method call parameters.
 */
public interface CallArgs {

    /**
     * Get the arguments of the method call.
     * @return
     */
    Object[] args();

    /**
     * Get the return type of the method.
     * @return {@link Caller#returnType()}
     */
    Type returnType();

    /**
     * Get the parameter types of the method.
     * @return {@link Caller#method()}
     */
    Type[] parameterTypes();

    /**
     * Get the caller object for the method.
     * @return
     */
    Caller<?> caller();

}

