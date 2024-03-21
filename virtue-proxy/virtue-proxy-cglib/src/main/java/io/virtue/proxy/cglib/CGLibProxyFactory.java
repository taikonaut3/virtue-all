package io.virtue.proxy.cglib;

import io.virtue.common.constant.Components;
import io.virtue.common.spi.ServiceProvider;
import io.virtue.proxy.AbstractProxyFactory;
import io.virtue.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Enhancer;

/**
 * Create Proxy By CGLIB.
 */
@ServiceProvider(Components.ProxyFactory.CGLIB)
public class CGLibProxyFactory extends AbstractProxyFactory {

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T doCreateProxy(Class<T> interfaceClass, InvocationHandler handler) throws Exception {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(interfaceClass.getClassLoader());
        enhancer.setSuperclass(interfaceClass);
        enhancer.setCallback(new CGLibMethodInterceptor(interfaceClass, handler));
        return (T) enhancer.create();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T doCreateProxy(T target, InvocationHandler handler) throws Exception {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(target.getClass().getClassLoader());
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new CGLibMethodInterceptor(target, handler));
        return (T) enhancer.create();
    }

}
