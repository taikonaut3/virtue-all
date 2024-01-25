package io.github.astro.virtue.config.manager;

import io.github.astro.virtue.common.util.ReflectUtil;
import io.github.astro.virtue.config.*;
import io.github.astro.virtue.config.annotation.BindingCaller;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/13 17:46
 */
public class ProtocolRegistryManager {

    private final Map<String, ProtocolRegistryWrapper> confMap = new ConcurrentHashMap<>();

    public ServerCaller<?> createServerCaller(BindingCaller register, Method method, RemoteService<?> remoteService) {
        ProtocolRegistryWrapper wrapper = confMap.get(register.protocol());
        Class<? extends ServerCaller> serverCaller = register.serverCaller();
        if (wrapper == null) {
            wrapper = registerProtocol(register);
        } else {
            if (wrapper.serverCallerConstructor() == null && checkCaller(serverCaller)) {
                Constructor<? extends ServerCaller> serverConstructor = ReflectUtil.finfConstructor(serverCaller, Method.class, RemoteService.class);
                if (serverConstructor != null) {
                    wrapper.serverCallerConstructor(serverConstructor);
                    wrapper.serverCaller(serverCaller);
                }
            }
        }
        if (wrapper.serverCallerConstructor() != null) {
            return ReflectUtil.createInstance(wrapper.serverCallerConstructor(), method, remoteService);
        }
        return null;
    }

    public ClientCaller<?> createClientCaller(BindingCaller register, Method method, RemoteCaller<?> remoteCaller) {
        ProtocolRegistryWrapper wrapper = confMap.get(register.protocol());
        Class<? extends ClientCaller> clientCaller = register.clientCaller();
        if (wrapper == null) {
            wrapper = registerProtocol(register);
        } else {
            if (wrapper.clientCallerConstructor() == null && checkCaller(clientCaller)) {
                Constructor<? extends ClientCaller> clientConstructor = ReflectUtil.finfConstructor(clientCaller, Method.class, RemoteCaller.class);
                if (clientConstructor != null) {
                    wrapper.clientCallerConstructor(clientConstructor);
                    wrapper.clientCaller(clientCaller);
                }
            }
        }
        if (wrapper.clientCallerConstructor() != null) {
            return ReflectUtil.createInstance(wrapper.clientCallerConstructor(), method, remoteCaller);
        }
        return null;
    }

    private ProtocolRegistryWrapper registerProtocol(BindingCaller bindingCaller) {
        String protocol = bindingCaller.protocol();
        Class<? extends ClientCaller> clientCaller = bindingCaller.clientCaller();
        Class<? extends ServerCaller> serverCaller = bindingCaller.serverCaller();
        ProtocolRegistryWrapper wrapper = new ProtocolRegistryWrapper();
        wrapper.protocol(protocol);
        if (!clientCaller.isInterface() && !Modifier.isAbstract(clientCaller.getModifiers())) {
            Constructor<? extends ClientCaller> clientConstructor = ReflectUtil.finfConstructor(clientCaller, Method.class, RemoteCaller.class);
            if (clientConstructor != null) {
                wrapper.clientCallerConstructor(clientConstructor);
                wrapper.clientCaller(clientCaller);
            }
        }
        if (!serverCaller.isInterface() && !Modifier.isAbstract(serverCaller.getModifiers())) {
            Constructor<? extends ServerCaller> serverConstructor = ReflectUtil.finfConstructor(serverCaller, Method.class, RemoteService.class);
            if (serverConstructor != null) {
                wrapper.serverCallerConstructor(serverConstructor);
                wrapper.serverCaller(serverCaller);
            }
        }
        confMap.put(protocol, wrapper);
        return wrapper;
    }

    private boolean checkCaller(Class<? extends Caller> caller) {
        return !caller.isInterface() && !Modifier.isAbstract(caller.getModifiers());
    }

    @Accessors(fluent = true)
    @Getter
    @Setter
    static class ProtocolRegistryWrapper {

        String protocol;

        Class<? extends ClientCaller> clientCaller;

        Constructor<? extends ClientCaller> clientCallerConstructor;

        Class<? extends ServerCaller> serverCaller;

        Constructor<? extends ServerCaller> serverCallerConstructor;

    }
}
