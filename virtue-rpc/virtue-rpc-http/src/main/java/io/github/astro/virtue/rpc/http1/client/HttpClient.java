package io.github.astro.virtue.rpc.http1.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.astro.virtue.common.exception.ConnectException;
import io.github.astro.virtue.common.exception.NetWorkException;
import io.github.astro.virtue.common.exception.RpcException;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.CallArgs;
import io.github.astro.virtue.config.Invocation;
import io.github.astro.virtue.config.Virtue;
import io.github.astro.virtue.rpc.http1.HttpParser;
import io.github.astro.virtue.rpc.http1.HttpProtocol;
import io.github.astro.virtue.serialization.Serializer;
import io.github.astro.virtue.transport.Response;
import io.github.astro.virtue.transport.ResponseFuture;
import io.github.astro.virtue.transport.channel.Channel;
import io.github.astro.virtue.transport.client.Client;
import io.vertx.core.AsyncResult;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;

import static io.github.astro.virtue.common.constant.Components.Serialize.JSON;
import static io.github.astro.virtue.rpc.http1.config.ContentType.APPLICATION_FORM_URLENCODED;
import static io.github.astro.virtue.rpc.http1.config.ContentType.APPLICATION_JSON;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/16 9:19
 */
public class HttpClient implements Client {

    public static final Logger logger = LoggerFactory.getLogger(HttpProtocol.class);

    private final WebClient webClient;

    private final ObjectMapper objectMapper;

    private final HttpParser httpParser;

    public HttpClient(URL url, HttpProtocol httpProtocol) {
        Virtue virtue = Virtue.getDefault();
        Vertx vertx = (Vertx) virtue.getDataOrPut("vertx", Vertx.vertx());
        webClient = WebClient.create(vertx);
        objectMapper = (ObjectMapper) virtue.getDataOrPut("objectMapper", new ObjectMapper());
        httpParser = (HttpParser) httpProtocol.parser();
    }

    public ResponseFuture send(HttpRequest<Buffer> request, Invocation invocation) {
        URL url = invocation.url();
        CallArgs callArgs = invocation.callArgs();
        Object body = httpParser.parseRequestBody(request.method().name(), invocation);
        ResponseFuture future = new ResponseFuture(url, callArgs);
        if (body != null) {
            // 根据 Content-Type 设置请求体
            String contentType = request.headers().get("Content-Type");
            send(url, request, Objects.requireNonNullElse(contentType, APPLICATION_JSON), body, future);
        } else {
            request.send(ar -> handleResponse(url, ar, future));
        }
        return future;
    }

    private void handleResponse(io.github.astro.virtue.common.url.URL url, AsyncResult<HttpResponse<Buffer>> ar, ResponseFuture future) {
        if (ar.succeeded()) {
            HttpResponse<Buffer> httpResponse = ar.result();
            if (httpResponse.statusCode() != 200) {
                RpcException rpcException = new RpcException(httpResponse.statusMessage());
                logger.error("Http code {}:{}", httpResponse.statusCode(), httpResponse.statusMessage());
                future.completeExceptionally(rpcException);
            } else {
                Response response = new Response(url, httpResponse);
                future.setResponse(response);
                Type returnType = future.returnType();
                if (returnType == response.getClass()) {
                    future.complete(response);
                } else if (returnType == response.message().getClass()) {
                    future.complete(response.message());
                } else {
                    String resBody = httpResponse.bodyAsString();
                    Serializer serializer = ExtensionLoader.loadService(Serializer.class, JSON);
                    Object body = serializer.convert(resBody, returnType);
                    future.complete(body);
                }
            }
        } else {
            future.completeExceptionally(ar.cause());
        }
    }

    private void send(io.github.astro.virtue.common.url.URL url, HttpRequest<Buffer> request, String contentType, Object body, ResponseFuture future) {
        switch (contentType) {
            case APPLICATION_JSON -> request.sendJson(body, ar -> handleResponse(url, ar, future));
            case APPLICATION_FORM_URLENCODED -> {
                MultiMap multiMap = MultiMap.caseInsensitiveMultiMap();
                Map<String, String> map = convertMap(body);
                multiMap.addAll(map);
                request.sendForm(multiMap, ar -> handleResponse(url, ar, future));
            }
            default ->
                    future.completeExceptionally(new IllegalArgumentException("Unsupported Content-Type Url:" + url));
        }
    }

    private Map<String, String> convertMap(Object obj) {
        return objectMapper.convertValue(obj, new TypeReference<>() {
        });
    }

    @Override
    public void close() throws NetWorkException {
        webClient.close();
    }

    @Override
    public boolean isActive() {
        return webClient != null;
    }

    @Override
    public void connect() throws ConnectException {
    }

    @Override
    public Channel channel() {
        throw new UnsupportedOperationException("Http UnSupport Get Remote channel");
    }

    @Override
    public String host() {
        throw new UnsupportedOperationException("Http UnSupport Get Remote host");
    }

    @Override
    public int port() {
        throw new UnsupportedOperationException("Http UnSupport Get Remote port");
    }

    @Override
    public InetSocketAddress toInetSocketAddress() {
        throw new UnsupportedOperationException("Http UnSupport Get Remote socketAddress");
    }
}
