package io.github.astro.virtue.proxy.cglib;

import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.proxy.AbstractProxyFactory;
import io.github.astro.virtue.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Enhancer;

import static io.github.astro.virtue.common.constant.Components.ProxyFactory.CGLIB;

@ServiceProvider(CGLIB)
public class CGlibProxyFactory extends AbstractProxyFactory {

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T doCreateProxy(Class<T> interfaceClass, InvocationHandler handler) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(interfaceClass.getClassLoader());
        enhancer.setSuperclass(interfaceClass);
        enhancer.setCallback(new CGlibMethodInterceptor(interfaceClass, handler));
        return (T) enhancer.create();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T doCreateProxy(T target, InvocationHandler handler) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(target.getClass().getClassLoader());
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new CGlibMethodInterceptor(target, handler));
        return (T) enhancer.create();
    }

}
