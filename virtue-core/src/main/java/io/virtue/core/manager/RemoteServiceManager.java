package io.virtue.core.manager;

import io.virtue.core.Callee;
import io.virtue.core.RemoteService;
import io.virtue.core.Virtue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RemoteService Manager
 */
public class RemoteServiceManager extends AbstractManager<RemoteService<?>> {

    public RemoteServiceManager(Virtue virtue) {
        super(virtue);
    }

    public Collection<RemoteService<?>> remoteServices() {
        return map.values();
    }

    public void register(RemoteService<?> remoteService) {
        register(remoteService.name(), remoteService);
    }

    public Callee<?> getServerCaller(String protocol, String path) {
        for (RemoteService<?> remoteService : remoteServices()) {
            Callee<?> caller = remoteService.getCallee(protocol, path);
            if (caller != null) {
                return caller;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> RemoteService<T> get(Class<T> interfaceType) {
        if (interfaceType.isAnnotationPresent(io.virtue.core.annotation.RemoteService.class)) {
            io.virtue.core.annotation.RemoteService annotation = interfaceType.getAnnotation(io.virtue.core.annotation.RemoteService.class);
            return (RemoteService<T>) get(annotation.value());

        }
        return null;
    }

    public List<Callee<?>> serverCallers() {
        return remoteServices().stream()
                .flatMap(container -> Arrays.stream(container.invokers()).map(caller -> (Callee<?>) caller))
                .collect(Collectors.toList());
    }

}
