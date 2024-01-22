package io.github.astro.rpc;

import io.github.astro.rpc.config.AbstractCallerContainer;
import io.github.astro.virtue.common.util.AssertUtil;
import io.github.astro.virtue.common.util.ReflectUtil;
import io.github.astro.virtue.config.RemoteService;
import io.github.astro.virtue.config.ServerCaller;
import io.github.astro.virtue.config.annotation.AutoRegister;
import io.github.astro.virtue.config.manager.ProtocolRegistryManager;
import lombok.NonNull;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@ToString
public class ComplexRemoteService<T> extends AbstractCallerContainer implements RemoteService<T> {

    private static final Class<io.github.astro.virtue.config.annotation.RemoteService> REMOTE_SERVICE_CLASS =
            io.github.astro.virtue.config.annotation.RemoteService.class;

    private final Map<String, ServerCaller<?>> providerCallerMap = new HashMap<>();

    private final Class<T> remoteServiceClass;

    private final T target;

    private String remoteServiceName;

    @SuppressWarnings("unchecked")
    public ComplexRemoteService(@NonNull T target) {
        AssertUtil.condition(checkRemoteService(target.getClass()), "remoteService this Method Only support @RemoteService modifier's Object");
        this.target = target;
        this.remoteServiceClass = (Class<T>) target.getClass();
        init();

    }

    public static boolean checkRemoteService(Class<?> remoteServiceClass) {
        return remoteServiceClass.isAnnotationPresent(REMOTE_SERVICE_CLASS);
    }

    @Override
    public void init() {
        // parse @RemoteService
        parseRemoteService();
        // parse @Callable
        parseCallable();
        virtue.registerRemoteService(this);
    }

    private void parseRemoteService() {
        io.github.astro.virtue.config.annotation.RemoteService service = remoteServiceClass.getAnnotation(REMOTE_SERVICE_CLASS);
        remoteApplication = virtue.applicationName();
        remoteServiceName = service.value();
        proxy = service.proxy();

    }

    private void parseCallable() {
        for (Method method : remoteServiceClass.getDeclaredMethods()) {
            AutoRegister autoRegister = ReflectUtil.findAnnotation(method, AutoRegister.class);
            if (autoRegister != null) {
                ProtocolRegistryManager registry = virtue.protocolRegistryManager();
                ServerCaller<?> caller = registry.createServerCaller(autoRegister, method, this);
                if (caller != null) {
                    callerMap.put(method, caller);
                    providerCallerMap.put(autoRegister.protocol() + caller.path(), caller);
                }

            }
        }
    }

    @Override
    public T target() {
        return target;
    }

    @Override
    public ServerCaller<?> getCaller(String protocol, String path) {
        return providerCallerMap.get(protocol + path);
    }

    @Override
    public String name() {
        return remoteServiceName;
    }

}
