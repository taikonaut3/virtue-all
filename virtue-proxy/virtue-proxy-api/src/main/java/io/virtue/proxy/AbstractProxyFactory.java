package io.virtue.proxy;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.virtue.common.util.ReflectionUtil.invokeObjectMethod;

/**
 * Abstract ProxyFactory.
 */
public abstract class AbstractProxyFactory implements ProxyFactory {

    private final static Map<Class<?>, Enhancer<?>> ENHANCER_MAP = new ConcurrentHashMap<>();

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

    @Override
    @SuppressWarnings("unchecked")
    public <T> Enhancer<T> getEnHancer(Class<T> type) {
        return (Enhancer<T>) ENHANCER_MAP.computeIfAbsent(type, this::createEnhancer);
    }

    protected abstract <T> T doCreateProxy(Class<T> interfaceClass, InvocationHandler handler) throws Exception;

    protected abstract <T> T doCreateProxy(T target, InvocationHandler handler) throws Exception;

    protected abstract <T> Enhancer<T> createEnhancer(Class<T> type);

    private InvocationHandler wrap(Object target, InvocationHandler invocationHandler) {
        return (proxy, method, args, superInvoker) -> {
            if (Object.class.equals(method.getDeclaringClass())) {
                return invokeObjectMethod(target, method, args);
            }
            return invocationHandler.invoke(proxy, method, args, superInvoker);
        };
    }

}
