package io.virtue.proxy.cglib;

import io.virtue.common.extension.spi.Extension;
import io.virtue.proxy.AbstractProxyFactory;
import io.virtue.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Enhancer;

import static io.virtue.common.constant.Components.ProxyFactory.CGLIB;
import static io.virtue.common.util.ClassUtil.getClassLoader;

/**
 * Create Proxy By CGLIB.
 */
@Extension(CGLIB)
public class CGLibProxyFactory extends AbstractProxyFactory {

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T doCreateProxy(Class<T> interfaceClass, InvocationHandler handler) throws Exception {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(getClassLoader(interfaceClass));
        enhancer.setSuperclass(interfaceClass);
        enhancer.setCallback(new CGLibMethodInterceptor(interfaceClass, handler));
        return (T) enhancer.create();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T doCreateProxy(T target, InvocationHandler handler) throws Exception {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(getClassLoader(target.getClass()));
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new CGLibMethodInterceptor(target, handler));
        return (T) enhancer.create();
    }

}
