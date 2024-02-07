package io.github.astro.virtue.rpc;

import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.util.AssertUtil;
import io.github.astro.virtue.common.util.GenerateUtil;
import io.github.astro.virtue.common.util.ReflectUtil;
import io.github.astro.virtue.config.CallerFactory;
import io.github.astro.virtue.config.RemoteService;
import io.github.astro.virtue.config.ServerCaller;
import io.github.astro.virtue.config.annotation.CallerFactoryProvider;
import io.github.astro.virtue.config.manager.Virtue;
import io.github.astro.virtue.rpc.config.AbstractCallerContainer;
import lombok.ToString;

import java.lang.reflect.Method;

@ToString
public class ComplexRemoteService<T> extends AbstractCallerContainer implements RemoteService<T> {

    private static final Class<io.github.astro.virtue.config.annotation.RemoteService> REMOTE_SERVICE_CLASS =
            io.github.astro.virtue.config.annotation.RemoteService.class;


    private final Class<T> remoteServiceClass;

    private final T target;

    private String remoteServiceName;

    @SuppressWarnings("unchecked")
    public ComplexRemoteService(Virtue virtue, T target) {
        super(virtue);
        AssertUtil.notNull(target);
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
        // parse ServerCaller
        parseServerCaller();
    }

    private void parseRemoteService() {
        io.github.astro.virtue.config.annotation.RemoteService service = remoteServiceClass.getAnnotation(REMOTE_SERVICE_CLASS);
        remoteApplication = virtue.applicationName();
        remoteServiceName = service.value();
        proxy = service.proxy();

    }

    private void parseServerCaller() {
        for (Method method : remoteServiceClass.getDeclaredMethods()) {
            CallerFactoryProvider factoryProvider = ReflectUtil.findAnnotation(method, CallerFactoryProvider.class);
            if (factoryProvider != null) {
                CallerFactory callerFactory = ExtensionLoader.loadService(CallerFactory.class, factoryProvider.value());
                ServerCaller<?> caller = callerFactory.createServerCaller(method, this);
                if (caller != null) {
                    callerMap.put(method, caller);
                    identificationCallerMap.put(caller.identification(), caller);
                    virtue.configManager().serverConfigManager().neededOpenProtocol(caller.protocol());
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
        return (ServerCaller<?>) identificationCallerMap.get(GenerateUtil.generateCallerIdentification(protocol, path));
    }

    @Override
    public String name() {
        return remoteServiceName;
    }

}
