package io.virtue.rpc.h2;

import io.virtue.common.exception.RpcException;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;
import io.virtue.core.RemoteService;
import io.virtue.rpc.h2.config.Http2Callable;
import io.virtue.rpc.support.AbstractCallee;
import io.virtue.transport.http.HttpMethod;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;
import java.util.List;

import static io.virtue.common.constant.Components.Protocol.HTTP2;

/**
 * Http2 protocol callee.
 */
@Getter
@Accessors(fluent = true)
public class Http2Callee extends AbstractCallee<Http2Callable> {

    private Http2Wrapper wrapper;

    public Http2Callee(Method method, RemoteService<?> remoteService) {
        super(method, remoteService, HTTP2, Http2Callable.class);
    }

    @Override
    protected void doInit() {
        wrapper = new Http2Wrapper(parsedAnnotation, this);
    }

    @Override
    protected URL createUrl(URL serverUrl) {
        URL url = super.createUrl(serverUrl);
        url.addParams(wrapper.parameterization());
        url.set(HttpMethod.ATTRIBUTE_KEY, wrapper.httpMethod());
        return url;
    }

    @Override
    public Object invoke(Invocation invocation) throws RpcException {
        Object result = doInvoke(invocation);
        sendResponse(invocation, result);
        if (result instanceof Exception e) {
            throw RpcException.unwrap(e);
        }
        return result;
    }

    @Override
    public List<String> pathList() {
        return URL.pathToList(wrapper.path());
    }
}
