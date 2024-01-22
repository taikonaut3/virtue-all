package io.github.astro.virtue.config;

import java.lang.annotation.Annotation;

/**
 * Server caller for handling remote service calls.
 *
 * @param <T> The type of annotation used to specify the remote service.
 */
public interface ServerCaller<T extends Annotation> extends Caller<T> {

    /**
     * Gets the remote service associated with this server caller.
     *
     * @return The remote service.
     */
    RemoteService<?> remoteService();

    /**
     * Gets the description of the server caller.
     *
     * @return The description.
     */
    String desc();

    /**
     * Gets the path associated with the server caller.
     *
     * @return The path.
     */
    String path();
}

