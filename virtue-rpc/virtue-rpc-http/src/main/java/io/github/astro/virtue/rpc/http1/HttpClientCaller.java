package io.github.astro.virtue.rpc.http1;

import io.github.astro.virtue.rpc.config.AbstractClientCaller;
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
import io.github.astro.virtue.rpc.http1.client.HttpClient;
import io.github.astro.virtue.rpc.http1.config.HttpCall;
import io.github.astro.virtue.transport.ResponseFuture;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static io.github.astro.virtue.common.constant.Components.Protocol.HTTP;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/9 10:40
 */
@Accessors(fluent = true)
@Getter
public class HttpClientCaller extends AbstractClientCaller<HttpCall> {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientCaller.class);

    private String httpMethod;

    private Map<String, String> headers;

    private Map<String, String> params;

    private String path;

    private HttpProtocol httpProtocol;

    private HttpParser httpParser;

    private HttpClient httpClient;

    public HttpClientCaller(@NonNull Method method, @NonNull RemoteCaller<?> remoteCaller) {
        super(method, remoteCaller, HTTP, HttpCall.class);
    }

    @Override
    protected void doInit() {
        httpMethod = parsedAnnotation.method();
        path = parsedAnnotation.path();
        httpProtocol = (HttpProtocol) protocolInstance;
        httpParser = (HttpParser) httpProtocol.parser();
        httpClient = (HttpClient) protocolInstance.openClient(url);
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
        List<String> paths = httpParser.pathToList(httpParser.parsePathVariables(path, method().getParameters(), callArgs.args()));
        url.replacePaths(paths);
        return super.call(callArgs);
    }

    @Override
    public ResponseFuture doCall(Invocation invocation) throws RpcException {
        URL url = invocation.url();
        CallArgs callArgs = invocation.callArgs();
        HttpRequest<Buffer> request = httpProtocol.createRequest(url, callArgs);
        try {
            return httpClient.send(request, invocation);
        } catch (Exception e) {
            logger.error("", e);
            throw new RpcException(e);
        }
    }

}
