package io.virtue.core.manager;

import io.virtue.common.url.URL;
import io.virtue.core.Callee;
import io.virtue.core.RemoteService;
import io.virtue.core.Virtue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RemoteService Manager.
 */
public class RemoteServiceManager extends AbstractManager<RemoteService<?>> {

    public RemoteServiceManager(Virtue virtue) {
        super(virtue);
    }

    /**
     * Get all remote services.
     *
     * @return
     */
    public Collection<RemoteService<?>> remoteServices() {
        return map.values();
    }

    /**
     * Register a remote service.
     *
     * @param remoteService
     */
    public void register(RemoteService<?> remoteService) {
        register(remoteService.name(), remoteService);
    }

    public Callee<?> getCallee(URL url) {
        for (RemoteService<?> remoteService : remoteServices()) {
            Callee<?> callee = remoteService.getCallee(url.protocol(), url.path());
            if (callee != null) {
                return callee;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> RemoteService<T> get(Class<T> type) {
        if (type.isAnnotationPresent(io.virtue.core.annotation.RemoteService.class)) {
            var annotation = type.getAnnotation(io.virtue.core.annotation.RemoteService.class);
            return (RemoteService<T>) get(annotation.value());

        }
        return null;
    }

    /**
     * Get all server callee.
     *
     * @return
     */
    public List<Callee<?>> allCallee() {
        return remoteServices().stream()
                .flatMap(container -> Arrays.stream(container.invokers()).map(caller -> (Callee<?>) caller))
                .collect(Collectors.toList());
    }

}
