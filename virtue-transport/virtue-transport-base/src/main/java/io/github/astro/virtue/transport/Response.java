package io.github.astro.virtue.transport;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.extension.AttributeKey;
import io.github.astro.virtue.common.url.URL;
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

}
