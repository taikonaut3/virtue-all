package io.virtue.rpc.http1_1;

import io.virtue.common.constant.Components;
import io.virtue.common.exception.RpcException;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;
import io.virtue.core.RemoteCaller;
import io.virtue.rpc.RpcFuture;
import io.virtue.rpc.http1_1.config.HttpCall;
import io.virtue.rpc.http1_1.config.HttpRequestWrapper;
import io.virtue.rpc.support.AbstractCaller;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * HttpClientCaller
 */
@Accessors(fluent = true)
@Getter
public class HttpCaller extends AbstractCaller<HttpCall> {

    private static final Logger logger = LoggerFactory.getLogger(HttpCaller.class);

    private HttpRequestWrapper wrapper;

    private HttpParser httpParser;

    public HttpCaller(Method method, RemoteCaller<?> remoteCaller) {
        super(method, remoteCaller, Components.Protocol.HTTP, HttpCall.class);
    }

    @Override
    protected void doInit() {
        httpParser = (HttpParser) protocolInstance.parser();
        wrapper = httpParser.methodParser().parse(method);
    }

    @Override
    public List<String> pathList() {
        return List.of(wrapper().path());
    }

    @Override
    public Object invoke(Invocation invocation) throws RpcException {
        HttpRequestWrapper requestWrapper = wrapper.deepCopy();
        url.replacePaths(URL.pathToList(httpParser.methodParser().parsePathVariables(invocation, requestWrapper)));
        url.addParams(httpParser.methodParser().parseParams(invocation, requestWrapper));
        url.attribute(HttpRequestWrapper.ATTRIBUTE_KEY).set(requestWrapper);
        return super.invoke(invocation);
    }

    @Override
    protected RpcFuture send(Invocation invocation) {
        RpcFuture future = super.send(invocation);
        future.completeConsumer(f -> ((HttpProtocol) protocolInstance).returnClient(f.client()));
        return future;
    }

}
