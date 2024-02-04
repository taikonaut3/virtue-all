package io.github.astro.virtue.config;

import io.github.astro.virtue.common.spi.ServiceInterface;
import io.github.astro.virtue.common.util.ReflectUtil;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.astro.virtue.common.constant.Components.Protocol.VIRTUE;

/**
 * CallerFactory is used to create different caller for different protocols.
 */
@ServiceInterface(VIRTUE)
public interface CallerFactory {

    /**
     * Create serverCaller.
     *
     * @param method
     * @param remoteService
     * @return serverCaller instance
     */
    ServerCaller<?> createServerCaller(Method method, RemoteService<?> remoteService);

    /**
     * Create clientCaller.
     *
     * @param method
     * @param remoteCaller
     * @return clientCaller instance
     */
    ClientCaller<?> createClientCaller(Method method, RemoteCaller<?> remoteCaller);

    /**
     * Get callerFactory instance by class.
     *
     * @param type
     * @return callerFactory instance
     */
    static CallerFactory get(Class<? extends CallerFactory> type) {
        return FactoryManager.get(type);
    }

    class FactoryManager {
        private static final Map<String, CallerFactory> factoryMap = new ConcurrentHashMap<>();

        static CallerFactory get(Class<? extends CallerFactory> type) {
            CallerFactory callerFactory = factoryMap.get(type.getName());
            if (callerFactory == null) {
                callerFactory = ReflectUtil.createInstance(type);
                factoryMap.put(type.getName(), callerFactory);
            }
            return callerFactory;
        }
    }

}
