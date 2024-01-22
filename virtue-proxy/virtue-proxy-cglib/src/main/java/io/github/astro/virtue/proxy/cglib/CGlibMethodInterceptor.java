package io.github.astro.virtue.proxy.cglib;

import io.github.astro.virtue.proxy.InvocationHandler;
import io.github.astro.virtue.proxy.SuperInvoker;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/17 14:26
 */
public class CGlibMethodInterceptor implements MethodInterceptor {

    private final Object target;

    private final InvocationHandler handler;

    public CGlibMethodInterceptor(Object target, InvocationHandler handler) {
        this.target = target;
        this.handler = handler;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        } else {
            return handler.invoke(proxy, method, args, superInvoker(obj, proxy, args));
        }
    }

    private SuperInvoker<?> superInvoker(Object obj, MethodProxy proxy, Object[] args) {
        if (target instanceof Class<?>) {
            return null;
        }
        return () -> proxy.invokeSuper(obj, args);
    }

    @Override
    public String toString() {
        return target.toString();
    }
}
