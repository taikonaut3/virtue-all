package io.virtue.core.manager;

import io.virtue.common.util.NetUtil;
import io.virtue.common.util.StringUtil;
import io.virtue.core.Caller;
import io.virtue.core.RemoteCaller;
import io.virtue.core.Virtue;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RemoteCaller Manager.
 */
public class RemoteCallerManager extends AbstractManager<List<RemoteCaller<?>>> {

    public RemoteCallerManager(Virtue virtue) {
        super(virtue);
    }

    @Override
    public void clear() {
        remoteCallers().forEach(RemoteCaller::stop);
        super.clear();
    }

    /**
     * Get all remote callers.
     *
     * @return
     */
    public Collection<RemoteCaller<?>> remoteCallers() {
        LinkedList<RemoteCaller<?>> remoteCallers = new LinkedList<>();
        for (List<RemoteCaller<?>> value : map.values()) {
            remoteCallers.addAll(value);
        }
        return remoteCallers;
    }

    /**
     * Register remote caller.
     *
     * @param remoteCaller
     */
    public synchronized void register(RemoteCaller<?> remoteCaller) {
        String key = remoteCaller.remoteApplication();
        if (StringUtil.isBlank(key)) {
            key = NetUtil.getAddress(remoteCaller.directAddress());
        }
        List<RemoteCaller<?>> remoteCallers = map.computeIfAbsent(key, k -> new LinkedList<>());
        remoteCallers.add(remoteCaller);
    }

    @SuppressWarnings("unchecked")
    public <T> RemoteCaller<T> get(Class<T> interfaceType) {
        if (interfaceType.isAnnotationPresent(io.virtue.core.annotation.RemoteCaller.class)) {
            var annotation = interfaceType.getAnnotation(io.virtue.core.annotation.RemoteCaller.class);
            for (RemoteCaller<?> remoteCaller : get(annotation.value())) {
                if (remoteCaller.targetInterface() == interfaceType) {
                    return (RemoteCaller<T>) remoteCaller;
                }
            }
        }
        return null;
    }

    /**
     * Get all caller.
     *
     * @return
     */
    public List<Caller<?>> allCaller() {
        return remoteCallers().stream()
                .flatMap(container -> Arrays.stream(container.invokers()).map(caller -> (Caller<?>) caller))
                .collect(Collectors.toList());
    }

}
