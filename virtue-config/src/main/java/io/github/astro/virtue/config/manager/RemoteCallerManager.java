package io.github.astro.virtue.config.manager;

import io.github.astro.virtue.config.RemoteCaller;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class RemoteCallerManager extends AbstractManager<List<RemoteCaller<?>>> {

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

}
