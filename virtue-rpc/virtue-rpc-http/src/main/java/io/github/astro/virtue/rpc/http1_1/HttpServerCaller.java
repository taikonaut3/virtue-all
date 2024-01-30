package io.github.astro.virtue.rpc.http1_1;

import io.github.astro.virtue.rpc.config.AbstractServerCaller;
import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.RemoteService;
import io.github.astro.virtue.config.annotation.Config;
import io.github.astro.virtue.config.config.ServerConfig;
import io.github.astro.virtue.config.manager.ServerConfigManager;
import io.github.astro.virtue.rpc.http1_1.config.HttpCallable;

import java.lang.reflect.Method;

import static io.github.astro.virtue.common.constant.Components.Protocol.HTTP;
import static io.github.astro.virtue.common.constant.Constant.DEFAULT_HTTP_PORT;

public class HttpServerCaller extends AbstractServerCaller<HttpCallable> {

    public HttpServerCaller(Method method, RemoteService<?> remoteService) {
        super(method, remoteService, HTTP, HttpCallable.class);
    }

    @Override
    protected void doInit() {

    }

    @Override
    public String path() {
        return parsedAnnotation.path();
    }

    @Override
    protected Config config() {
        return parsedAnnotation.config();
    }

    @Override
    protected URL createUrl() {
        ServerConfigManager serverConfigManager = virtue.configManager().serverConfigManager();
        ServerConfig serverConfig = serverConfigManager.get(protocol);
        URL url = serverConfig.toUrl();
        url.addParameter(Key.CLASS, remoteService().target().getClass().getName());
        url.addParameters(parameterization());
        return url;
    }

    @Override
    protected ServerConfig defaultServerConfig() {
        return new ServerConfig(HTTP,DEFAULT_HTTP_PORT);
    }
}
