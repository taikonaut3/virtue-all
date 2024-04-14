package io.virtue.transport;

import io.virtue.common.constant.Key;
import io.virtue.common.extension.AttributeKey;
import io.virtue.common.url.URL;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Rpc request.
 */
@Data
@Accessors(fluent = true)
public class Request implements Envelope {

    public static final AttributeKey<Request> ATTRIBUTE_KEY = AttributeKey.of(Key.REQUEST);


    private boolean oneway;

    private URL url;

    private Object message;

    public Request() {
    }

    public Request(URL url, Object message) {
        this.oneway = url.getBooleanParam(Key.ONEWAY);
        this.url = url;
        this.message = message;

    }

}
