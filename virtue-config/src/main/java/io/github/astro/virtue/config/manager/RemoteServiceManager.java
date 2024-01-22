package io.github.astro.virtue.config.manager;

import io.github.astro.virtue.config.RemoteService;
import io.github.astro.virtue.config.ServerCaller;

import java.util.Collection;

public class RemoteServiceManager extends AbstractManager<RemoteService<?>> {

    public Collection<RemoteService<?>> getRemoteService() {
        return map.values();
    }

    public void register(RemoteService<?> remoteService) {
        register(remoteService.name(), remoteService);
    }

    public ServerCaller<?> getServerCaller(String protocol, String path) {
        for (RemoteService<?> remoteService : getRemoteService()) {
            ServerCaller<?> caller = remoteService.getCaller(protocol, path);
            if (caller != null) {
                return caller;
            }
        }
        return null;
    }

}
