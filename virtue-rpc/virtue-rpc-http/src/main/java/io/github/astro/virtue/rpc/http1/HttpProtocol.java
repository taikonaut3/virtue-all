package io.github.astro.virtue.rpc.http1;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.CallArgs;
import io.github.astro.virtue.config.Virtue;
import io.github.astro.virtue.rpc.http1.client.HttpClient;
import io.github.astro.virtue.rpc.http1.config.HttpMethod;
import io.github.astro.virtue.rpc.http1.server.HttpServer;
import io.github.astro.virtue.rpc.protocol.Protocol;
import io.github.astro.virtue.rpc.protocol.ProtocolParser;
import io.github.astro.virtue.transport.client.Client;
import io.github.astro.virtue.transport.code.Codec;
import io.github.astro.virtue.transport.server.Server;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.github.astro.virtue.common.constant.Components.Protocol.HTTP;

/**
 * HttpProtocol
 */
@ServiceProvider(HTTP)
public class HttpProtocol implements Protocol<HttpRequest<Buffer>, HttpResponse<Buffer>> {

    public static final Logger logger = LoggerFactory.getLogger(HttpProtocol.class);

    private final WebClient webClient;
    private final HttpParser httpParser;
    private volatile HttpClient httpClient;
    private volatile HttpServer httpServer;

    public HttpProtocol() {
        Vertx vertx = (Vertx) Virtue.getDefault().getDataOrPut("vertx", Vertx.vertx());
        webClient = WebClient.create(vertx);
        httpParser = new HttpParser();
    }

    @Override
    public String protocol() {
        return HTTP;
    }

    @Override
    public HttpRequest<Buffer> createRequest(URL url, Object payload) {
        String httpMethod = url.getParameter(Key.HTTP_METHOD, HttpMethod.GET);
        io.vertx.core.http.HttpMethod method = io.vertx.core.http.HttpMethod.valueOf(httpMethod);
        HttpRequest<Buffer> request = webClient.request(method, url.port(), url.host(), url.path());
        request.putHeader("url", url.toString());
        CallArgs callArgs = (CallArgs) payload;
        HttpClientCaller caller = (HttpClientCaller) callArgs.caller();
        Parameter[] parameters = caller.method().getParameters();
        Optional<Map<String, String>> optionalMap = Optional.ofNullable(httpParser.parseParams(parameters, callArgs.args()));
        Map<String, String> params = Optional.ofNullable(caller.params()).orElseGet(HashMap::new);
        optionalMap.ifPresent(params::putAll);
        Optional.of(params).ifPresent(p -> p.forEach(request::addQueryParam));
        Optional.ofNullable(caller.headers()).ifPresent(h -> h.forEach(request::putHeader));
        return request;
    }

    @Override
    public HttpResponse<Buffer> createResponse(URL url, Object payload) {
        return null;
    }

    @Override
    public Client openClient(URL url) {
        if (httpClient == null) {
            synchronized (this) {
                if (httpClient == null) {
                    httpClient = new HttpClient(url, this);
                }
            }
        }
        return httpClient;
    }

    @Override
    public Server openServer(URL url) {
        if (httpServer == null) {
            synchronized (this) {
                if (httpServer == null) {
                    httpServer = new HttpServer(url);
                }
            }
        }
        return httpServer;
    }

    @Override
    public Codec serverCodec() {
        throw new UnsupportedOperationException("Used vertx http server");
    }

    @Override
    public Codec clientCodec() {
        throw new UnsupportedOperationException("Used vertx http client");
    }

    @Override
    public ProtocolParser parser() {
        return httpParser;
    }
}
