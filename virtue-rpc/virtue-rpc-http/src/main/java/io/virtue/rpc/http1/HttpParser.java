package io.virtue.rpc.http1;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;
import io.virtue.common.constant.Components;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.core.Invocation;
import io.virtue.core.Invoker;
import io.virtue.rpc.RpcFuture;
import io.virtue.rpc.protocol.ProtocolParser;
import io.virtue.serialization.Serializer;
import io.virtue.transport.Request;
import io.virtue.transport.Response;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HttpParser
 */
@Data
@Accessors(fluent = true)
public class HttpParser implements ProtocolParser {


    @Override
    public Invocation parseRequestBody(Request request) {
        return null;
    }

    @Override
    public Object parseResponseBody(Response response) {
        FullHttpResponse httpResponse = (FullHttpResponse) response.message();
        ByteBuf byteBuf = httpResponse.content();
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        Serializer serializer = ExtensionLoader.loadService(Serializer.class, Components.Serialization.JSON);
        RpcFuture future = RpcFuture.getFuture(String.valueOf(response.id()));
        Invoker<?> invoker = future.invocation().invoker();
        Type type = invoker.returnType();
        Object object = serializer.deserialize(bytes, invoker.returnClass());
        return serializer.convert(object, type);
    }

    public Map<String, String> parseHeaders(String[] headers) {
        return getStringMap(headers);
    }

    public Map<String, String> parseParams(String[] params) {
        return getStringMap(params);
    }

    private Map<String, String> getStringMap(String[] params) {
        if (params == null || params.length == 0) {
            return new HashMap<>();
        }
        return Arrays.stream(params)
                .map(pair -> pair.split("="))
                .filter(keyValue -> keyValue.length == 2)
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0].trim(),
                        keyValue -> keyValue[1].trim()
                ));
    }

}
