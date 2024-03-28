package io.virtue.transport;

import io.virtue.common.constant.Key;
import io.virtue.common.extension.AttributeKey;
import io.virtue.common.url.URL;
import io.virtue.common.util.DateUtil;
import io.virtue.common.util.StringUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Rpc request.
 */
@Data
@Accessors(fluent = true)
public class Request implements Envelope {

    public static final AttributeKey<Request> ATTRIBUTE_KEY = AttributeKey.get(Key.REQUEST);

    private static final AtomicLong INCREASE = new AtomicLong(0);

    private Long id;

    private boolean oneway;

    private URL url;

    private Object message;

    public Request() {
    }

    public Request(URL url, Object message) {
        String uniqueId = url.getParam(Key.UNIQUE_ID);
        if (StringUtil.isBlank(uniqueId)) {
            id = INCREASE.getAndIncrement();
            String timestamp = DateUtil.format(LocalDateTime.now(), DateUtil.COMPACT_FORMAT);
            url.addParam(Key.TIMESTAMP, timestamp);
            url.addParam(Key.ENVELOPE, Key.REQUEST);
            url.addParam(Key.UNIQUE_ID, String.valueOf(id));
        } else {
            id = Long.parseLong(uniqueId);
        }
        this.oneway = url.getBooleanParam(Key.ONEWAY);
        this.url = url;
        this.message = message;

    }

}
