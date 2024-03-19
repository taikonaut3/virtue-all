package io.virtue.core.manager;

import io.virtue.core.ClientCaller;
import io.virtue.core.RemoteCaller;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RemoteCaller Manager
 */
public class RemoteCallerManager extends AbstractManager<List<RemoteCaller<?>>> {

    public RemoteCallerManager(Virtue virtue) {
        super(virtue);
    }

    public Collection<RemoteCaller<?>> remoteCallers() {
        LinkedList<RemoteCaller<?>> remoteCallers = new LinkedList<>();
        for (List<RemoteCaller<?>> value : map.values()) {
            remoteCallers.addAll(value);
        }
        return remoteCallers;
    }

    public ClientCaller<?> getClientCaller(String identification) {
        for (RemoteCaller<?> remoteCaller : remoteCallers()) {
            ClientCaller<?> caller = (ClientCaller<?>) remoteCaller.getCaller(identification);
            if (caller != null) {
                return caller;
            }
        }
        return null;
    }

    public synchronized void register(RemoteCaller<?> remoteCaller) {
        List<RemoteCaller<?>> remoteCallers = map.computeIfAbsent(remoteCaller.remoteApplication(), k -> new LinkedList<>());
        remoteCallers.add(remoteCaller);
    }

    @SuppressWarnings("unchecked")
    public <T> RemoteCaller<T> get(Class<T> interfaceType) {
        if (interfaceType.isAnnotationPresent(io.virtue.core.annotation.RemoteCaller.class)) {
            io.virtue.core.annotation.RemoteCaller annotation = interfaceType.getAnnotation(io.virtue.core.annotation.RemoteCaller.class);
            for (RemoteCaller<?> remoteCaller : get(annotation.value())) {
                if (remoteCaller.targetInterface() == interfaceType) {
                    return (RemoteCaller<T>) remoteCaller;
                }
            }
        }
        return null;
    }

    public List<ClientCaller<?>> clientCallers() {
        return remoteCallers().stream()
                .flatMap(container -> Arrays.stream(container.callers()).map(caller -> (ClientCaller<?>) caller))
                .collect(Collectors.toList());
    }

}
