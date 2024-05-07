package io.virtue.proxy;

import java.lang.reflect.UndeclaredThrowableException;

import static io.virtue.common.util.ReflectionUtil.invokeObjectMethod;

/**
 * Abstract ProxyFactory.
 */
public abstract class AbstractProxyFactory implements ProxyFactory {

    @Override
    public <T> T createProxy(Class<T> interfaceClass, InvocationHandler handler) {
        try {
            if (!interfaceClass.isInterface()) {
                throw new IllegalArgumentException(interfaceClass + "Is Not Interface," + "The Method Only Support Interface");
            }
            return doCreateProxy(interfaceClass, wrap(interfaceClass, handler));
        } catch (Exception e) {
            throw new UndeclaredThrowableException(e, "Create Proxy fail for interface: " + interfaceClass.getName());
        }
    }

    @Override
    public <T> T createProxy(T target, InvocationHandler handler) {
        try {
            return doCreateProxy(target, wrap(target, handler));
        } catch (Exception e) {
            throw new UndeclaredThrowableException(e, "Create Proxy fail for target: " + target.getClass().getName());
        }
    }

    protected abstract <T> T doCreateProxy(Class<T> interfaceClass, InvocationHandler handler) throws Exception;

    protected abstract <T> T doCreateProxy(T target, InvocationHandler handler) throws Exception;

    private InvocationHandler wrap(Object target, InvocationHandler invocationHandler) {
        return (proxy, method, args, superInvoker) -> {
            if (Object.class.equals(method.getDeclaringClass())) {
                return invokeObjectMethod(target, method, args);
            }
            return invocationHandler.invoke(proxy, method, args, superInvoker);
        };
    }

}
