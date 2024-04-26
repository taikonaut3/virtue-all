package io.virtue.rpc.h1.support;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.virtue.common.constant.Constant;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.util.StringUtil;
import io.virtue.serialization.Serializer;
import io.virtue.transport.util.TransportUtil;

import java.util.Map;

import static io.virtue.common.constant.Components.Compression.*;
import static io.virtue.common.constant.Components.Serialization.*;
import static io.virtue.common.util.StringUtil.getStringMap;

/**
 * Http Util.
 */
public final class HttpUtil extends TransportUtil {

    private static final String IDENTIFY = "virtue-rpc/" + Constant.VERSION;

    private static final Map<CharSequence, CharSequence> CONTENT_TYPE_MAPPING = Map.of(
            "application/json", JSON,
            "application/msgpack", MSGPACK,
            "application/protobuf", PROTOBUF
    );

    /**
     * Build a common client request header.
     *
     * @return
     */
    public static Map<CharSequence, CharSequence> regularRequestHeaders() {
        String acceptEncoding = String.join(",", GZIP, DEFLATE, LZ4, SNAPPY);
        String accept = String.join(",", CONTENT_TYPE_MAPPING.keySet());
        return Map.of(
                HttpHeaderNames.ACCEPT_ENCODING, acceptEncoding,
                HttpHeaderNames.ACCEPT, accept,
                HttpHeaderNames.USER_AGENT, IDENTIFY
        );
    }

    /**
     * Build a common server request header.
     *
     * @return
     */
    public static Map<CharSequence, CharSequence> regularResponseHeaders() {
        return Map.of(
                HttpHeaderNames.SERVER, IDENTIFY
        );
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
