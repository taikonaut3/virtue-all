package io.virtue.proxy.bytebuddy;

import io.virtue.proxy.InvocationHandler;
import io.virtue.proxy.SuperInvoker;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * Used to handle interception of interface and instance methods.
 */
public class MethodInterceptor implements InvocationHandler {

    private final InvocationHandler invocationHandler;

    public MethodInterceptor(InvocationHandler invocationHandler) {
        this.invocationHandler = invocationHandler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args, SuperInvoker<?> superInvoker) throws Throwable {
        return invocationHandler.invoke(proxy, method, args, superInvoker);
    }

    /**
     * Used to handle interception of interface methods.
     */
    public class InterfaceInterceptor {

        /**
         * Bytebuddy interceptor method.
         *
         * @param proxy
         * @param method
         * @param args
         * @return
         * @throws Throwable
         */
        @RuntimeType
        public Object intercept(@This Object proxy, @Origin Method method, @AllArguments Object[] args) throws Throwable {
            return invoke(proxy, method, args, () -> null);
        }
    }

    /**
     * Used to handle interception of instance methods.
     */
    public class InstanceInterceptor {

        /**
         * Bytebuddy interceptor method.
         *
         * @param proxy
         * @param method
         * @param args
         * @param callable
         * @return
         * @throws Throwable
         */
        @RuntimeType
        public Object intercept(@This Object proxy, @Origin Method method, @AllArguments Object[] args,
                                @SuperCall Callable<?> callable) throws Throwable {
            return invoke(proxy, method, args, callable::call);
        }
    }

}