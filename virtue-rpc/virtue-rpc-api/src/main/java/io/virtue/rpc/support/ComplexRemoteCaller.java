package io.virtue.rpc.support;

import com.esotericsoftware.reflectasm.MethodAccess;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.util.AssertUtil;
import io.virtue.common.util.NetUtil;
import io.virtue.common.util.ReflectionUtil;
import io.virtue.core.*;
import io.virtue.core.annotation.InvokerFactory;
import io.virtue.core.support.TransferableInvocation;
import io.virtue.proxy.InvocationHandler;
import io.virtue.proxy.ProxyFactory;
import io.virtue.proxy.SuperInvoker;
import lombok.ToString;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

/**
 * Default RemoteCaller Impl.
 *
 * @param <T> interface type
 */
@ToString
public class ComplexRemoteCaller<T> extends AbstractInvokerContainer implements RemoteCaller<T> {

    private static final Class<io.virtue.core.annotation.RemoteCaller> REMOTE_CALL_CLASS =
            io.virtue.core.annotation.RemoteCaller.class;

    private final Class<T> targetInterface;

    private T proxyInstance;

    private boolean lazyDiscover;

    private InetSocketAddress directAddress;

    private FallBackerWrapper<T> fallBackerWrapper;

    public ComplexRemoteCaller(Virtue virtue, Class<T> target) {
        super(virtue);
        AssertUtil.notNull(target);
        AssertUtil.condition(checkRemoteCall(target), "RemoteCaller's this Method only support @RemoteCall modifier's Interface");
        this.targetInterface = target;
        init();
    }

    private static boolean checkRemoteCall(Class<?> interfaceType) {
        return interfaceType.isInterface() && interfaceType.isAnnotationPresent(REMOTE_CALL_CLASS);
    }

    @Override
    public void init() {
        // parse @RemoteCaller
        parseRemoteCaller();
        // parse ClientCaller
        parseClientCaller();
        // create proxy
        ProxyFactory proxyFactory = ExtensionLoader.loadService(ProxyFactory.class, proxy);
        proxyInstance = proxyFactory.createProxy(targetInterface, new ClientInvocationHandler(this));
    }

    @Override
    public T get() {
        return proxyInstance;
    }

    @Override
    public boolean lazyDiscover() {
        return lazyDiscover;
    }

    @Override
    public InetSocketAddress directAddress() {
        return directAddress;
    }

    @Override
    public void fallBacker(T fallBacker) {
        this.fallBackerWrapper = new FallBackerWrapper<>(fallBacker);
    }

    @Override
    public T fallBacker() {
        return fallBackerWrapper == null ? null : fallBackerWrapper.fallBacker;
    }

    @Override
    public Object invokeFallBack(Invocation invocation) {
        return fallBackerWrapper.invoke(invocation);
    }

    @Override
    public Class<T> targetInterface() {
        return targetInterface;
    }

    private void parseRemoteCaller() {
        var remoteCaller = targetInterface.getAnnotation(REMOTE_CALL_CLASS);
        String value = remoteCaller.value();
        try {
            directAddress = NetUtil.toInetSocketAddress(value);
        } catch (Exception e) {
            remoteApplication = value;
        }
        proxy = remoteCaller.proxy();
        lazyDiscover = remoteCaller.lazyDiscover();
    }

    private void parseClientCaller() {
        for (Method method : targetInterface.getDeclaredMethods()) {
            InvokerFactory factoryProvider = ReflectionUtil.findAnnotation(method, InvokerFactory.class);
            if (factoryProvider != null) {
                var invokerFactory = ExtensionLoader.loadService(io.virtue.core.InvokerFactory.class, factoryProvider.value());
                Caller<?> caller = invokerFactory.createCaller(method, this);
                if (caller != null) {
                    invokers.put(method, caller);
                }
            }
        }
    }

    /**
     * Client InvocationHandler,Rpc caller invoke.
     */
    static class ClientInvocationHandler implements InvocationHandler {

        private final RemoteCaller<?> remoteCaller;

        ClientInvocationHandler(RemoteCaller<?> remoteCaller) {
            this.remoteCaller = remoteCaller;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args, SuperInvoker<?> superInvoker) throws Throwable {
            Invoker<?> invoker = remoteCaller.getInvoker(method);
            if (invoker != null) {
                var invocation = new TransferableInvocation((Caller<?>) invoker, args);
                return invoker.invoke(invocation);
            }
            return null;
        }

    }

    static class FallBackerWrapper<T> {
        private final T fallBacker;

        private final MethodAccess methodAccess;

        FallBackerWrapper(T fallBacker) {
            this.fallBacker = fallBacker;
            methodAccess = MethodAccess.get(fallBacker.getClass());
        }

        Object invoke(Invocation invocation) {
            Method method = invocation.invoker().method();
            int index = methodAccess.getIndex(method.getName(), method.getParameterTypes());
            return methodAccess.invoke(fallBacker, index, invocation.args());
        }
    }
}
