package io.github.astro.virtue.transport;

import io.github.astro.virtue.common.url.URL;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class Response implements Envelope {

    public static final byte SUCCESS = 0, ERROR = -1, TIMEOUT = 3;

    private byte code;

    private URL url;

    private Object message;

    public Response() {
    }

    public Response(URL url, Object message) {
        this.url = url;
        this.message = message;
    }

}
