package io.virtue.rpc.h2;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.virtue.common.constant.Constant;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.util.StringUtil;
import io.virtue.core.Invocation;
import io.virtue.core.Invoker;
import io.virtue.rpc.h2.config.Body;
import io.virtue.serialization.Serializer;

import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;

import static io.virtue.common.constant.Components.Serialization.*;
import static io.virtue.transport.util.TransportUtil.getStringMap;

/**
 * Http Util.
 */
public final class HttpUtil {

    private static final Map<CharSequence, CharSequence> CONTENT_TYPE_MAPPING = Map.of(
            "application/json", JSON,
            "application/msgpack", MSGPACK,
            "application/protobuf", PROTOBUF
    );

    /**
     * Find request body.
     *
     * @param invocation
     * @return
     */
    public static Object findBody(Invocation invocation) {
        Parameter[] parameters = invocation.invoker().method().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.isAnnotationPresent(Body.class)) {
                return invocation.args()[i];
            }
        }
        return null;
    }

    /**
     * Find request body parameter.
     *
     * @param invoker
     * @return
     */
    public static Parameter findBodyParameter(Invoker<?> invoker) {
        Parameter[] parameters = invoker.method().getParameters();
        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(Body.class)) {
                return parameter;
            }
        }
        return null;
    }

    /**
     * Build a common client request header.
     *
     * @return
     */
    public static Map<CharSequence, CharSequence> commonClientHeaders() {
        Map<CharSequence, CharSequence> headers = new LinkedHashMap<>();
        headers.put(HttpHeaderNames.ACCEPT_ENCODING, "gzip, deflate");
        headers.put(HttpHeaderNames.USER_AGENT, "virtue-rpc/" + Constant.VERSION);
        headers.put(HttpHeaderNames.ACCEPT, "application/json");
        return headers;
    }

    /**
     * Build a common server request header.
     *
     * @return
     */
    public static Map<CharSequence, CharSequence> commonServerHeaders() {
        Map<CharSequence, CharSequence> headers = new LinkedHashMap<>();
        headers.put(HttpHeaderNames.SERVER, "virtue-rpc/" + Constant.VERSION);
        return headers;
    }

    /**
     * Parse headers.
     *
     * @param headers
     * @return
     */
    public static Map<CharSequence, CharSequence> parseHeaders(String[] headers) {
        return getStringMap(headers, ":");
    }

    /**
     * Serialize body.
     *
     * @param contentType
     * @param body
     * @return
     */
    public static byte[] serialize(CharSequence contentType, Object body) {
        if (body == null) {
            return new byte[0];
        }
        return getSerializer(contentType).serialize(body);
    }

    public static Serializer getSerializer(CharSequence contentType) {
        CharSequence serializerName = CONTENT_TYPE_MAPPING.get(contentType);
        if (StringUtil.isBlank(serializerName)) {
            throw new UnsupportedOperationException("Unsupported content type: " + contentType + "'s serialization");
        }
        return ExtensionLoader.loadExtension(Serializer.class, serializerName.toString());
    }
}
