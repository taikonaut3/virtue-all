package io.github.taikonaut3.virtue.proxy.bytebuddy;

import io.github.taikonaut3.virtue.proxy.InvocationHandler;
import io.github.taikonaut3.virtue.proxy.SuperInvoker;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class MethodInterceptor implements InvocationHandler {

    private final InvocationHandler invocationHandler;

    public MethodInterceptor(InvocationHandler invocationHandler) {
        this.invocationHandler = invocationHandler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args, SuperInvoker<?> superInvoker) throws Throwable {
        return invocationHandler.invoke(proxy, method, args, superInvoker);
    }

    public class InterfaceInterceptor {
        @RuntimeType
        public Object intercept(@This Object proxy, @Origin Method method, @AllArguments Object[] args) throws Throwable {
            return invoke(proxy, method, args, () -> null);
        }
    }

    public class InstanceInterceptor {
        @RuntimeType
        public Object intercept(@This Object proxy, @Origin Method method, @AllArguments Object[] args, @SuperCall Callable<?> callable) throws Throwable {
            return invoke(proxy, method, args, callable::call);
        }

    }

}