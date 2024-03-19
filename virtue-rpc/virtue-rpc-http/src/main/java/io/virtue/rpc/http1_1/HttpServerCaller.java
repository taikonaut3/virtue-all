package io.virtue.rpc.http1_1;

import io.virtue.core.RemoteService;
import io.virtue.core.annotation.Config;
import io.virtue.rpc.support.AbstractServerCaller;
import io.virtue.rpc.http1_1.config.HttpCallable;
import io.virtue.common.constant.Components;

import java.lang.reflect.Method;
import java.util.List;

public class HttpServerCaller extends AbstractServerCaller<HttpCallable> {

    public HttpServerCaller(Method method, RemoteService<?> remoteService) {
        super(method, remoteService, Components.Protocol.HTTP, HttpCallable.class);
    }

    @Override
    protected void doInit() {

    }

    @Override
    public String path() {
        return parsedAnnotation.path();
    }

    @Override
    public List<String> pathList() {
        return List.of(parsedAnnotation.path());
    }

    @Override
    protected Config config() {
        return parsedAnnotation.config();
    }

}
