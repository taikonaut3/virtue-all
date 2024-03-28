package io.virtue.transport;

import io.virtue.common.constant.Key;
import io.virtue.common.extension.AttributeKey;
import io.virtue.common.url.URL;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Rpc response.
 */
@Data
@Accessors(fluent = true)
public class Response implements Envelope {

    public static final AttributeKey<Response> ATTRIBUTE_KEY = AttributeKey.get(Key.RESPONSE);

    public static final byte SUCCESS = 0, ERROR = -1, TIMEOUT = 3;

    private byte code;

    private URL url;

    private Object message;

    public Response() {
    }

    public Response(URL url, Object message) {
        url.addParam(Key.ENVELOPE, Key.RESPONSE);
        this.url = url;
        this.message = message;
    }

    public Response(byte code, URL url, Object message) {
        this(url, message);
        code(code);
    }

    /**
     * Create a success response.
     * @param url
     * @param message
     * @return
     */
    public static Response success(URL url, Object message) {
        return new Response(SUCCESS, url, message);
    }

    /**
     * Create a error response.
     * @param url
     * @param message
     * @return
     */
    public static Response error(URL url, Object message) {
        return new Response(ERROR, url, message);
    }

}
