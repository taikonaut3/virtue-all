package io.github.astro.virtue.rpc.virtue;

import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.config.*;

import java.lang.reflect.Method;

import static io.github.astro.virtue.common.constant.Components.Protocol.VIRTUE;

/**
 * VirtueCallerFactory
 */
@ServiceProvider(VIRTUE)
public class VirtueCallerFactory implements CallerFactory {
    @Override
    public ServerCaller<?> createServerCaller(Method method, RemoteService<?> remoteService) {
        return new VirtueServerCaller(method, remoteService);
    }

    @Override
    public ClientCaller<?> createClientCaller(Method method, RemoteCaller<?> remoteCaller) {
        return new VirtueClientCaller(method, remoteCaller);
    }
}
