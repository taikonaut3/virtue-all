package io.github.astro.virtue.config;

import java.lang.annotation.Annotation;

/**
 * Client caller for making remote service calls.
 *
 * @param <T> The type of annotation used to specify the remote service.
 */
public interface ClientCaller<T extends Annotation> extends Caller<T>, DirectRemoteCall, CallOptions {

    /**
     * Gets the remote caller associated with this client caller.
     *
     * @return The remote caller.
     */
    RemoteCaller<?> remoteCaller();

    /**
     * Gets the direct URL for making the remote service call.
     *
     * @return The direct URL.
     */
    String directUrl();

}

