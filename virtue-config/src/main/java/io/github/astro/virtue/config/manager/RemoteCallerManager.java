package io.github.astro.virtue.config.manager;

import io.github.astro.virtue.config.RemoteCaller;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * RemoteCaller Manager
 */
public class RemoteCallerManager extends AbstractManager<List<RemoteCaller<?>>> {

    public RemoteCallerManager(Virtue virtue) {
        super(virtue);
    }

    public Collection<RemoteCaller<?>> getRemoteCallers() {
        LinkedList<RemoteCaller<?>> remoteCallers = new LinkedList<>();
        for (List<RemoteCaller<?>> value : map.values()) {
            remoteCallers.addAll(value);
        }
        return remoteCallers;
    }

    public synchronized void register(RemoteCaller<?> remoteCaller) {
        List<RemoteCaller<?>> remoteCallers = map.computeIfAbsent(remoteCaller.remoteApplication(), k -> new LinkedList<>());
        remoteCallers.add(remoteCaller);
    }

    @SuppressWarnings("unchecked")
    public <T> RemoteCaller<T> get(Class<T> interfaceType) {
        if (interfaceType.isAnnotationPresent(io.github.astro.virtue.config.annotation.RemoteCaller.class)) {
            io.github.astro.virtue.config.annotation.RemoteCaller annotation = interfaceType.getAnnotation(io.github.astro.virtue.config.annotation.RemoteCaller.class);
            for (RemoteCaller<?> remoteCaller : get(annotation.value())) {
                if (remoteCaller.targetInterface() == interfaceType) {
                    return (RemoteCaller<T>) remoteCaller;
                }
            }
        }
        return null;
    }

}
