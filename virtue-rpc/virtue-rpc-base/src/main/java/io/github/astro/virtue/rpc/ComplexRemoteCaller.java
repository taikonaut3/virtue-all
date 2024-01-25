package io.github.astro.virtue.rpc;

import io.github.astro.virtue.rpc.config.AbstractCallerContainer;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.util.AssertUtil;
import io.github.astro.virtue.common.util.ReflectUtil;
import io.github.astro.virtue.config.Caller;
import io.github.astro.virtue.config.RemoteCaller;
import io.github.astro.virtue.config.annotation.BindingCaller;
import io.github.astro.virtue.config.manager.ProtocolRegistryManager;
import io.github.astro.virtue.proxy.ProxyFactory;
import lombok.NonNull;
import lombok.ToString;

import java.lang.reflect.Method;

@ToString
public class ComplexRemoteCaller<T> extends AbstractCallerContainer implements RemoteCaller<T> {

    private static final Class<io.github.astro.virtue.config.annotation.RemoteCaller> REMOTE_CALL_CLASS =
            io.github.astro.virtue.config.annotation.RemoteCaller.class;

    private final Class<T> targetInterface;

    private T proxyInstance;

    public ComplexRemoteCaller(@NonNull Class<T> targetInterface) {
        AssertUtil.condition(checkRemoteCall(targetInterface), "RemoteCaller's this Method only support @RemoteCall modifier's Interface");
        this.targetInterface = targetInterface;
        init();
    }

    private static boolean checkRemoteCall(Class<?> interfaceType) {
        return interfaceType.isInterface() && interfaceType.isAnnotationPresent(REMOTE_CALL_CLASS);
    }

    @Override
    public void init() {
        // parse @RemoteCaller
        parseRemoteCaller();
        // parse @Call
        parseCaller();
        // create proxy
        ProxyFactory proxyFactory = ExtensionLoader.loadService(ProxyFactory.class, proxy);
        proxyInstance = proxyFactory.createProxy(targetInterface, new ClientInvocationHandler(this));
        virtue.registerRemoteCaller(this);
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

    private void parseCaller() {
        for (Method method : targetInterface.getDeclaredMethods()) {
            BindingCaller bindingCaller = ReflectUtil.findAnnotation(method, BindingCaller.class);
            if (bindingCaller != null) {
                ProtocolRegistryManager registry = virtue.protocolRegistryManager();
                Caller<?> caller = registry.createClientCaller(bindingCaller, method, this);
                if (caller != null) {
                    callerMap.put(method, caller);
                }
            }
        }
    }
}
