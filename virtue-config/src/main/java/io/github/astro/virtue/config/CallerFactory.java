package io.github.astro.virtue.config;

import io.github.astro.virtue.common.spi.ServiceInterface;
import io.github.astro.virtue.common.util.ReflectUtil;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.astro.virtue.common.constant.Components.Protocol.VIRTUE;

@ServiceInterface(VIRTUE)
public interface CallerFactory {

    Map<String, CallerFactory> factoryMap = new ConcurrentHashMap<>();

    static CallerFactory get(Class<? extends CallerFactory> type) {
        CallerFactory callerFactory = factoryMap.get(type.getName());
        if (callerFactory == null) {
            callerFactory = ReflectUtil.createInstance(type);
            factoryMap.put(type.getName(), callerFactory);
        }
        return callerFactory;
    }

    ServerCaller<?> createServerCaller(Method method, RemoteService<?> remoteService);

    ClientCaller<?> createClientCaller(Method method, RemoteCaller<?> remoteCaller);

}
