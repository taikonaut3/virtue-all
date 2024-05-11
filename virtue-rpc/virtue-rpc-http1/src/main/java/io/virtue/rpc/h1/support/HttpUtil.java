package io.virtue.rpc.h1.support;

import io.virtue.common.extension.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.serialization.Serializer;
import io.virtue.transport.http.HttpHeaderNames;
import io.virtue.transport.http.MediaType;
import io.virtue.transport.http.h1.HttpResponse;
import io.virtue.transport.util.TransportUtil;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

import static io.virtue.common.constant.Components.Compression.*;
import static io.virtue.common.constant.Version.version;
import static io.virtue.common.util.StringUtil.getStringMap;
import static io.virtue.transport.http.HttpHeaderNames.CONTENT_TYPE;

/**
 * Http Util.
 */
public final class HttpUtil extends TransportUtil {

    private static final String IDENTIFY = "virtue-rpc/" + version();

    private static final String ACCEPT_TYPE = String.join(",", Arrays.stream(MediaType.values()).map(MediaType::getName).toList());

    /**
     * Build a common client request header.
     *
     * @return
     */
    public static Map<CharSequence, CharSequence> regularRequestHeaders() {
        String acceptEncoding = String.join(",", GZIP, DEFLATE, LZ4, SNAPPY);
        return Map.of(
                HttpHeaderNames.ACCEPT_ENCODING, acceptEncoding,
                HttpHeaderNames.ACCEPT, ACCEPT_TYPE,
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
     * encode body.
     *
     * @param contentType
     * @param body
     * @return
     */
    public static byte[] encodeBody(CharSequence contentType, Object body) {
        if (body == null) {
            return new byte[0];
        }
        return getSerializer(contentType).serialize(body);
    }

    /**
     * Decode body.
     *
     * @param response
     * @param returnType
     * @return
     */
    public static Object decodeBody(HttpResponse response, Type returnType) {
        CharSequence contentType = response.headers().get(CONTENT_TYPE);
        Serializer serializer = getSerializer(contentType);
        return serializer.deserialize(response.data(), returnType);
    }

    public static Serializer getSerializer(CharSequence contentType) {
        MediaType mediaType = MediaType.of(contentType);
        if (mediaType == null) {
            throw new UnsupportedOperationException("Unsupported content type: " + contentType + "'s serialization");
        }
        return ExtensionLoader.loadExtension(Serializer.class, mediaType.getSerialization());
    }

    /**
     * Convert url to http url.
     *
     * @param invocation
     * @return
     */
    public static URL createRequestURL(HttpInvocation invocation) {
        URL url = invocation.url();
        URL requestURL = new URL(url.protocol(), url.address());
        requestURL.addPaths(url.paths());
        invocation.params().forEach((k, v) -> requestURL.addParam(k.toString(), v.toString()));
        return requestURL;
    }
}
