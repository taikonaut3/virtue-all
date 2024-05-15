package io.virtue.transport.http;

import io.virtue.common.util.StringUtil;
import lombok.Getter;

import static io.virtue.common.constant.Components.Serialization.*;

/**
 * Http media type.
 */
@Getter
public enum MediaType {

    APPLICATION_JSON("application/json", JSON),
    APPLICATION_MSGPACK("application/msgpack", MSGPACK),
    APPLICATION_PROTOBUF("application/protobuf", PROTOBUF);

    private final CharSequence name;

    private final String serialization;

    MediaType(String name, String serialization) {
        this.name = name;
        this.serialization = serialization;
    }

    /**
     * Get MediaType by name.
     *
     * @param name
     * @return
     */
    public static MediaType of(CharSequence name) {
        for (MediaType value : MediaType.values()) {
            if (StringUtil.equals(name, value.getName())) {
                return value;
            }
        }
        return null;
    }

}
