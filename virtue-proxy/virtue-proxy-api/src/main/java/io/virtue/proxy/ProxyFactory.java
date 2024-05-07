package io.virtue.proxy;

import io.virtue.common.extension.spi.Extensible;

import static io.virtue.common.constant.Components.ProxyFactory.JDK;

/**
 * Factory for creating proxies.
 */
@Extensible(JDK)
public interface ProxyFactory {

    /**
     * Create a new proxy instance that implements the specified interface using the given invocation handler.
     *
     * @param interfaceClass the interface to be implemented by the proxy object
     * @param handler        the invocation handler to dispatch method invocations to
     * @param <T>
     * @return a new proxy object that implements the specified interface
     */
    <T> T createProxy(Class<T> interfaceClass, InvocationHandler handler);

    /**
     * Create a new proxy instance around the specified target object using the given invocation handler.
     *
     * @param target  the object to be proxied
     * @param handler the invocation handler to dispatch method invocations to
     * @param <T>
     * @return a new proxy object that wraps the specified target object
     */
    <T> T createProxy(T target, InvocationHandler handler);

    default <T> Enhancer<T> getEnHancer(Class<T> type) {
        return null;
    }

}
