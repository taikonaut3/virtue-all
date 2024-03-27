package io.virtue.core;

import java.lang.annotation.Annotation;

/**
 * Server callee for handling remote service calls.
 *
 * @param <T> The type of annotation used to specify the remote service.
 */
public interface Callee<T extends Annotation> extends Invoker<T> {

    /**
     * Gets the remote service associated with this server callee.
     *
     * @return remoteService instance
     */
    RemoteService<?> remoteService();

    /**
     * Gets the description of the server callee.
     *
     * @return method description
     */
    String desc();

}

