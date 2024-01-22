package io.github.astro.virtue.proxy.jdk;

import io.github.astro.virtue.proxy.InvocationHandler;
import io.github.astro.virtue.proxy.SuperInvoker;

import java.lang.reflect.Method;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/17 14:18
 */
public class JdkInvocationHandler implements java.lang.reflect.InvocationHandler {

    private final Object target;

    private final InvocationHandler handler;

    public JdkInvocationHandler(Object target, InvocationHandler handler) {
        this.target = target;
        this.handler = handler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        } else {
            return handler.invoke(proxy, method, args, superInvoker(method, args));
        }
    }

    private SuperInvoker<?> superInvoker(Method method, Object[] args) {
        if (target instanceof Class<?>) {
            return null;
        }
        return () -> method.invoke(target, args);
    }

    @Override
    public String toString() {
        return target.toString();
    }
}
