package io.virtue.proxy;

import io.virtue.common.spi.ServiceInterface;

import static io.virtue.common.constant.Components.ProxyFactory.JDK;

/**
 * Factory for creating proxies.
 */
@ServiceInterface(JDK)
public interface ProxyFactory {

    /**
     *
     * @param interfaceClass the interface to be implemented by the proxy object
     * @param handler        the invocation handler to dispatch method invocations to
     * @return a new proxy object that implements the specified interface
     * @param <T>
     */
    <T> T createProxy(Class<T> interfaceClass, InvocationHandler handler);

    /**
     * Create a new proxy instance around the specified target object using the given invocation handler.
     *
     * @param target  the object to be proxied
     * @param handler the invocation handler to dispatch method invocations to
     * @return a new proxy object that wraps the specified target object
     * @param <T>
     */
    <T> T createProxy(T target, InvocationHandler handler);

}
