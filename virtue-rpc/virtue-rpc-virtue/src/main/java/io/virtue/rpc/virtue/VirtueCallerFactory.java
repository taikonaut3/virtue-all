package io.virtue.rpc.virtue;

import io.virtue.common.spi.ServiceProvider;
import io.virtue.common.constant.Components;
import io.virtue.core.*;

import java.lang.reflect.Method;

/**
 * VirtueCallerFactory
 */
@ServiceProvider(Components.Protocol.VIRTUE)
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
