package io.github.astro.virtue.rpc.http1_1;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.exception.RpcException;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.common.util.StringUtil;
import io.github.astro.virtue.config.CallArgs;
import io.github.astro.virtue.config.Invocation;
import io.github.astro.virtue.config.RemoteCaller;
import io.github.astro.virtue.config.RemoteUrl;
import io.github.astro.virtue.config.annotation.Config;
import io.github.astro.virtue.config.annotation.Options;
import io.github.astro.virtue.config.config.ClientConfig;
import io.github.astro.virtue.rpc.config.AbstractClientCaller;
import io.github.astro.virtue.rpc.http1_1.config.HttpCall;
import io.github.astro.virtue.transport.RpcFuture;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

import static io.github.astro.virtue.common.constant.Components.Protocol.HTTP;

/**
 * HttpClientCaller
 */
@Accessors(fluent = true)
@Getter
public class HttpClientCaller extends AbstractClientCaller<HttpCall> {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientCaller.class);

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
    protected URL createUrl() {
        String address = remoteApplication;
        if (!StringUtil.isBlank(directUrl)) {
            address = directUrl;
        }
        RemoteUrl remoteUrl = new RemoteUrl(protocol, address);
        remoteUrl.addPath(path);
        remoteUrl.addParameter(Key.HTTP_METHOD, httpMethod);
        remoteUrl.addParameters(parameterization());
        return remoteUrl;
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
    protected ClientConfig defaultClientConfig() {
        return new ClientConfig(HTTP);
    }

    @Override
    protected RpcFuture doCall(Invocation invocation) {
        RpcFuture future = super.doCall(invocation);
        future.completeConsumer(f -> ((HttpProtocol) protocolInstance).returnClient(f.client()));
        return future;
    }
}
