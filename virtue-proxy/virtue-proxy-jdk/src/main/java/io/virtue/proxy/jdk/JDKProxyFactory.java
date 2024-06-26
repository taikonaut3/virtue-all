package io.virtue.proxy.jdk;

import io.virtue.common.extension.spi.Extension;
import io.virtue.proxy.AbstractProxyFactory;
import io.virtue.proxy.InvocationHandler;

import java.lang.reflect.Proxy;

import static io.virtue.common.constant.Components.ProxyFactory.JDK;

/**
 * Create Proxy By JDK.
 */
@Extension(JDK)
public class JDKProxyFactory extends AbstractProxyFactory {

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T doCreateProxy(Class<T> interfaceClass, InvocationHandler handler) throws Exception {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                new JDKInvocationHandler(interfaceClass, handler)
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T doCreateProxy(T target, InvocationHandler handler) throws Exception {
        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new JDKInvocationHandler(target, handler)
        );
    }

}
