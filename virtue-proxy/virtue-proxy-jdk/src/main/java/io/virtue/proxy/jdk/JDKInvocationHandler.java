package io.virtue.proxy.jdk;

import io.virtue.proxy.InvocationHandler;
import io.virtue.proxy.SuperInvoker;

import java.lang.reflect.Method;

/**
 * JDK InvocationHandler Impl.
 */
public class JDKInvocationHandler implements java.lang.reflect.InvocationHandler {

    private final Object target;

    private final InvocationHandler handler;

    public JDKInvocationHandler(Object target, InvocationHandler handler) {
        this.target = target;
        this.handler = handler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return handler.invoke(proxy, method, args, superInvoker(method, args));

    }

    private SuperInvoker<?> superInvoker(Method method, Object[] args) {
        if (target instanceof Class<?>) {
            return () -> null;
        }
        return () -> method.invoke(target, args);
    }
}
