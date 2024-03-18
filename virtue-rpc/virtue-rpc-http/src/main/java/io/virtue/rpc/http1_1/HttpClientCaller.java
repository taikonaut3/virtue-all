package io.virtue.rpc.http1_1;

import io.virtue.common.exception.RpcException;
import io.virtue.common.url.URL;
import io.virtue.config.CallArgs;
import io.virtue.config.Invocation;
import io.virtue.config.RemoteCaller;
import io.virtue.rpc.RpcFuture;
import io.virtue.rpc.support.AbstractClientCaller;
import io.virtue.rpc.http1_1.config.HttpCall;
import io.virtue.rpc.http1_1.config.HttpRequestWrapper;
import io.virtue.common.constant.Components;
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
public class HttpClientCaller extends AbstractClientCaller<HttpCall> {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientCaller.class);

    private HttpRequestWrapper wrapper;

    private HttpParser httpParser;

    public HttpClientCaller(Method method, RemoteCaller<?> remoteCaller) {
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
    public Object call(URL url, CallArgs callArgs) throws RpcException {
        HttpRequestWrapper requestWrapper = wrapper.deepCopy();
        url.replacePaths(URL.pathToList(httpParser.methodParser().parsePathVariables(callArgs, requestWrapper)));
        url.addParameters(httpParser.methodParser().parseParams(callArgs, requestWrapper));
        url.attribute(HttpRequestWrapper.ATTRIBUTE_KEY).set(requestWrapper);
        return super.call(url,callArgs);
    }

    @Override
    protected RpcFuture send(Invocation invocation) {
        RpcFuture future = super.send(invocation);
        future.completeConsumer(f -> ((HttpProtocol) protocolInstance).returnClient(f.client()));
        return future;
    }

}
