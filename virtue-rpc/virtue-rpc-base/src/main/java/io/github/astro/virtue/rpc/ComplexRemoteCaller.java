package io.github.astro.virtue.rpc;

import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.util.AssertUtil;
import io.github.astro.virtue.common.util.ReflectUtil;
import io.github.astro.virtue.config.CallerFactory;
import io.github.astro.virtue.config.ClientCaller;
import io.github.astro.virtue.config.RemoteCaller;
import io.github.astro.virtue.config.annotation.RegistryCallerFactory;
import io.github.astro.virtue.config.manager.Virtue;
import io.github.astro.virtue.proxy.ProxyFactory;
import io.github.astro.virtue.rpc.config.AbstractCallerContainer;
import lombok.ToString;

import java.lang.reflect.Method;

@ToString
public class ComplexRemoteCaller<T> extends AbstractCallerContainer implements RemoteCaller<T> {

    private static final Class<io.github.astro.virtue.config.annotation.RemoteCaller> REMOTE_CALL_CLASS =
            io.github.astro.virtue.config.annotation.RemoteCaller.class;

    private final Class<T> targetInterface;

    private T proxyInstance;

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
    public Class<T> targetInterface() {
        return targetInterface;
    }

    private void parseRemoteCaller() {
        io.github.astro.virtue.config.annotation.RemoteCaller remoteCaller = targetInterface.getAnnotation(REMOTE_CALL_CLASS);
        remoteApplication = remoteCaller.value();
        proxy = remoteCaller.proxy();
    }

    private void parseClientCaller() {
        for (Method method : targetInterface.getDeclaredMethods()) {
            RegistryCallerFactory registryCallerFactory = ReflectUtil.findAnnotation(method, RegistryCallerFactory.class);
            if (registryCallerFactory != null) {
                CallerFactory callerFactory = CallerFactory.get(registryCallerFactory.value());
                ClientCaller<?> caller = callerFactory.createClientCaller(method, this);
                if (caller != null) {
                    callerMap.put(method, caller);
                }
            }
        }
    }
}
