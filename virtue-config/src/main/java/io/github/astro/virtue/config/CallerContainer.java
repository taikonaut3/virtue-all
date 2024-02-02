package io.github.astro.virtue.config;

import io.github.astro.virtue.config.manager.Virtue;

import java.lang.reflect.Method;

/**
 * The Caller Container For managing the caller
 */
public interface CallerContainer extends Lifecycle {

    /**
     * Gets the name of the microservice application used to locate the target IP.
     */
    String remoteApplication();

    /**
     * Gets an array of callers associated with the container.
     */
    Caller<?>[] callers();

    /**
     * Gets the caller associated with the specified method.
     *
     * @param method the method for which to get the associated caller
     * @return the caller associated with the method, or null if not found
     */
    Caller<?> getCaller(Method method);

    /**
     * The Caller container Proxy type
     */
    String proxy();

    Virtue virtue();

}

