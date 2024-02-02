package io.github.astro.virtue.rpc.http1_1;

import io.github.astro.virtue.config.*;

import java.lang.reflect.Method;

/**
 * @Author WenBo Zhou
 * @Date 2024/2/1 13:36
 */
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
