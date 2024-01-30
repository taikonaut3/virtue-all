package io.github.astro.virtue.rpc.http1_1;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.spi.ServiceProvider;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.common.util.BeanConverter;
import io.github.astro.virtue.config.CallArgs;
import io.github.astro.virtue.rpc.handler.*;
import io.github.astro.virtue.rpc.http1_1.config.ContentType;
import io.github.astro.virtue.rpc.objectfactory.ClientPool;
import io.github.astro.virtue.rpc.protocol.AbstractProtocol;
import io.github.astro.virtue.serialization.Serializer;
import io.github.astro.virtue.transport.client.Client;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.astro.virtue.common.constant.Components.Protocol.HTTP;
import static io.github.astro.virtue.common.constant.Components.Serialize.JSON;
import static io.netty.handler.codec.http.HttpMethod.*;

/**
 * HttpProtocol
 */
@ServiceProvider(HTTP)
public class HttpProtocol extends AbstractProtocol<FullHttpRequest, FullHttpResponse> {

    public static final Logger logger = LoggerFactory.getLogger(HttpProtocol.class);

    private static final List<HttpMethod> NO_BODY_METHODS = List.of(GET, HEAD, DELETE, OPTIONS, TRACE);

    private final Map<String, ClientPool> clientPools = new ConcurrentHashMap<>();

    private final HttpParser httpParser;

    public HttpProtocol() {
        super(HTTP,
                null,
                null,
                new DefaultChannelHandlerChain(new ClientHeartBeatChannelHandler(), new ResponseChannelHandler(), new ClientChannelHandler()),
                new DefaultChannelHandlerChain(new ServerHeartBeatChannelHandler(), new RequestChannelHandler(), new ServerChannelHandler()),
                new HttpParser());
        httpParser = (HttpParser) protocolParser;
    }

    @Override
    public Client openClient(URL url) {
        String key = url.address();
        ClientPool clientPool;
        clientPool = clientPools.get(key);
        if (clientPool == null) {
            synchronized (this) {
                if (clientPools.get(key) == null) {
                    clientPool = new ClientPool(transporter, clientCodec, clientHandler);
                    clientPools.put(key, clientPool);
                }
            }
        }
        assert clientPool != null;
        return clientPool.get(url);
    }

    @Override
    public FullHttpRequest createRequest(URL url, CallArgs args) {
        HttpClientCaller caller = (HttpClientCaller) args.caller();
        String httpMethodStr = url.getParameter(Key.HTTP_METHOD, io.github.astro.virtue.rpc.http1_1.config.HttpMethod.GET);
        HttpMethod httpMethod = valueOf(httpMethodStr);
        ByteBuf byteBuf = Unpooled.EMPTY_BUFFER;
        String contentType = Optional.ofNullable(caller.headers())
                .map(headers -> headers.get(HttpHeaderNames.CONTENT_TYPE.toString()))
                .orElse(ContentType.APPLICATION_JSON);
        if (!NO_BODY_METHODS.contains(httpMethod)) {
            Object body = httpParser.parseRequestBody(args.caller().method().getParameters(), args.args());
            if (body != null) {
                byteBuf = convertBody(body, contentType);
            }
        }
        DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, httpMethod, url.pathAndParams(), byteBuf);
        httpRequest.headers().add(HttpHeaderNames.CONTENT_TYPE, contentType);
        Optional.ofNullable(caller.headers()).ifPresent(h -> h.keySet().forEach(key -> httpRequest.headers().add(key, h.get(key))));
        return httpRequest;
    }

    @Override
    public FullHttpResponse createResponse(URL url, Object payload) {
        return null;
    }

    public ByteBuf convertBody(Object data, String contentType) {
        if (contentType.equals(ContentType.APPLICATION_X_WWW_FORM_URLENCODED)) {
            // 处理 x-www-form-urlencoded 类型的请求
            Map<String, String> formData = BeanConverter.convertToMap(data);
            String formStr = URL.mapToUrlString(formData);
            return Unpooled.copiedBuffer(formStr, StandardCharsets.UTF_8);
        } else if (contentType.equals(ContentType.APPLICATION_JSON)) {
            // 处理 application/json 类型的请求
            Serializer serializer = ExtensionLoader.loadService(Serializer.class, JSON);
            byte[] jsonBytes = serializer.serialize(data);
            return Unpooled.copiedBuffer(jsonBytes);
        } else if (contentType.startsWith(ContentType.MULTIPART_FORM_DATA)) {
            // 处理 multipart/form-data 类型的请求
            return Unpooled.EMPTY_BUFFER;
        } else {
            // 其他类型的请求处理（如 application/xml 等）
            // 根据实际情况进行处理
            return Unpooled.EMPTY_BUFFER;
        }
    }

    public void returnClient(Client client) {
        String address = client.address();
        ClientPool clientPool = clientPools.get(address);
        clientPool.returnClient(client);
    }

}
