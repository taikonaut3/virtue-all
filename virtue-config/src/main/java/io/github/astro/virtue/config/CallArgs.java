package io.github.astro.virtue.config;

import java.lang.reflect.Type;

/**
 * Encapsulates the arguments and return type of a method call.
 */
public interface CallArgs {

    /**
     * Get the arguments of the method call.
     */
    Object[] args();

    /**
     * Get the return type of the method.
     */
    Type returnType();

    /**
     * Get the parameter types of the method.
     */
    Type[] parameterTypes();

    /**
     * Get the caller object for the method.
     */
    Caller<?> caller();

}

