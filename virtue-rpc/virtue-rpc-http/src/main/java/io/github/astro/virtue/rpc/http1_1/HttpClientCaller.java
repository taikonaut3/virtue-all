package io.github.astro.virtue.rpc.http1_1;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.exception.RpcException;
import io.github.astro.virtue.common.url.Parameter;
import io.github.astro.virtue.config.CallArgs;
import io.github.astro.virtue.config.Invocation;
import io.github.astro.virtue.config.RemoteCaller;
import io.github.astro.virtue.config.annotation.Config;
import io.github.astro.virtue.config.annotation.Options;
import io.github.astro.virtue.rpc.RpcFuture;
import io.github.astro.virtue.rpc.config.AbstractClientCaller;
import io.github.astro.virtue.rpc.http1_1.config.HttpCall;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static io.github.astro.virtue.common.constant.Components.Protocol.HTTP;

/**
 * HttpClientCaller
 */
@Accessors(fluent = true)
@Getter
public class HttpClientCaller extends AbstractClientCaller<HttpCall> {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientCaller.class);

    @Parameter(Key.HTTP_METHOD)
    private String httpMethod;

    private Map<String, String> headers;

    private Map<String, String> params;

    private String path;

    private HttpParser httpParser;

    public HttpClientCaller(Method method, RemoteCaller<?> remoteCaller) {
        super(method, remoteCaller, HTTP, HttpCall.class);
    }

    @Override
    protected void doInit() {
        httpMethod = parsedAnnotation.method();
        path = parsedAnnotation.path();
        httpParser = (HttpParser) protocolInstance.parser();
        headers = httpParser.parseHeaders(parsedAnnotation.headers());
        params = httpParser.parseParams(parsedAnnotation.params());
    }

    @Override
    protected Config config() {
        return parsedAnnotation.config();
    }

    @Override
    public List<String> pathList() {
        return List.of(path);
    }

    @Override
    protected Options options() {
        return parsedAnnotation.options();
    }

    @Override
    public Object call(CallArgs callArgs) throws RpcException {
        url.replacePaths(httpParser.parsePaths(this, callArgs));
        url.addParameters(httpParser.parseParams(this, callArgs));
        return super.call(callArgs);
    }

    @Override
    protected RpcFuture doCall(Invocation invocation) {
        RpcFuture future = super.doCall(invocation);
        future.completeConsumer(f -> ((HttpProtocol) protocolInstance).returnClient(f.client()));
        return future;
    }
}
