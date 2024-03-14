package io.virtue.rpc.http1_1;

import io.virtue.common.spi.ServiceProvider;
import io.virtue.common.constant.Components;
import io.virtue.config.*;

import java.lang.reflect.Method;

@ServiceProvider(Components.Protocol.HTTP)
public class HttpCallerFactory implements CallerFactory {
    @Override
    public ServerCaller<?> createServerCaller(Method method, RemoteService<?> remoteService) {
        return new HttpServerCaller(method, remoteService);
    }

    @Override
    public ClientCaller<?> createClientCaller(Method method, RemoteCaller<?> remoteCaller) {
        return new HttpClientCaller(method, remoteCaller);
    }
}
