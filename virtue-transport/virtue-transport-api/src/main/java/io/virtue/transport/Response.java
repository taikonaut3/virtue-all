package io.virtue.transport;

import io.virtue.common.constant.Key;
import io.virtue.common.extension.AttributeKey;
import io.virtue.common.url.URL;
import lombok.Data;
import lombok.experimental.Accessors;

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
        url.addParameter(Key.ENVELOPE, Key.RESPONSE);
        this.url = url;
        this.message = message;
    }

    public Response(byte code, URL url, Object message) {
        this(url, message);
        code(code);
    }

}
