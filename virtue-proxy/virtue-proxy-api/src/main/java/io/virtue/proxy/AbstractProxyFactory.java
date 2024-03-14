package io.virtue.proxy;

public abstract class AbstractProxyFactory implements ProxyFactory {

    @Override
    public <T> T createProxy(Class<T> interfaceClass, InvocationHandler handler) {
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException(interfaceClass + "Is Not Interface," + "The Method Only Support Interface");
        }
        return doCreateProxy(interfaceClass, wrap(interfaceClass, handler));
    }

    @Override
    public <T> T createProxy(T target, InvocationHandler handler) {
        return doCreateProxy(target, wrap(target, handler));
    }

    protected abstract <T> T doCreateProxy(Class<T> interfaceClass, InvocationHandler handler);

    protected abstract <T> T doCreateProxy(T target, InvocationHandler handler);

    private InvocationHandler wrap(Object target, InvocationHandler invocationHandler) {
        return (proxy, method, args, superInvoker) -> {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(target, args);
            }
            return invocationHandler.invoke(proxy, method, args, superInvoker);
        };
    }

}
