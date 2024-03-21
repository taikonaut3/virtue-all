package io.virtue.proxy.cglib;

import io.virtue.proxy.InvocationHandler;
import io.virtue.proxy.SuperInvoker;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * CGLib MethodInterceptor Impl.
 */
public class CGLibMethodInterceptor implements MethodInterceptor {

    private final Object target;

    private final InvocationHandler handler;

    public CGLibMethodInterceptor(Object target, InvocationHandler handler) {
        this.target = target;
        this.handler = handler;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return handler.invoke(proxy, method, args, superInvoker(obj, proxy, args));

    }

    private SuperInvoker<?> superInvoker(Object obj, MethodProxy proxy, Object[] args) {
        if (target instanceof Class<?>) {
            return () -> null;
        }
        return () -> proxy.invokeSuper(obj, args);
    }

}
