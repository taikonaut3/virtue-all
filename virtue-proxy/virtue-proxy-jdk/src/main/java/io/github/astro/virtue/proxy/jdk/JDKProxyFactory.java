package io.github.astro.virtue.proxy.jdk;

import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.proxy.AbstractProxyFactory;
import io.github.astro.virtue.proxy.InvocationHandler;

import java.lang.reflect.Proxy;

import static io.github.astro.virtue.common.constant.Components.ProxyFactory.JDK;

@ServiceProvider(JDK)
public class JDKProxyFactory extends AbstractProxyFactory {

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T doCreateProxy(Class<T> interfaceClass, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass},
                new JdkInvocationHandler(interfaceClass, handler));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T doCreateProxy(T target, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(),
                new JdkInvocationHandler(target, handler));
    }

}
