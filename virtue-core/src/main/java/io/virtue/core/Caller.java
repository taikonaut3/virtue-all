package io.virtue.core;

import io.virtue.common.url.URL;
import io.virtue.common.util.StringUtil;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Client caller for making remote service calls.
 *
 * @param <T> The type of annotation used to specify the remote service.
 */
public interface Caller<T extends Annotation> extends Invoker<T>, Options {

    /**
     * Gets the remote caller associated with this client caller.
     *
     * @return remote caller instance
     */
    RemoteCaller<?> remoteCaller();

    /**
     * Convert RegistryConfig to URL.
     *
     * @return all registry url core
     */
    List<URL> registryConfigUrls();

    /**
     * Whether it is a direct connection call,{@link io.virtue.core.annotation.Options#url()}is the correct configuration.
     *
     * @return
     */
    default boolean isDirectInvoke() {
        return !StringUtil.isBlank(directUrl());
    }
}

