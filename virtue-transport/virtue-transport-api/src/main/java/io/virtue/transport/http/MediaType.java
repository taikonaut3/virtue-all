package io.virtue.transport.http;

import java.util.Objects;

import static io.virtue.common.constant.Components.Serialization.*;

/**
 * Http media type.
 */
public enum MediaType {

    APPLICATION_JSON("application/json", JSON),
    APPLICATION_MSGPACK("application/msgpack", MSGPACK),
    APPLICATION_PROTOBUF("application/protobuf", PROTOBUF);

    private final CharSequence name;

    private final String serialization;

    private MediaType(String name, String serialization) {
        this.name = name;
        this.serialization = serialization;
    }

    public static MediaType of(CharSequence name) {
        for (MediaType value : MediaType.values()) {
            if (Objects.equals(value.getName(), name)) {
                return value;
            }
        }
        return null;
    }

    public CharSequence getName() {
        return name;
    }

    public String getSerialization() {
        return serialization;
    }
}
