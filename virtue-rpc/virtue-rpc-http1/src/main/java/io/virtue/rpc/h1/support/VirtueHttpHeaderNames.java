package io.virtue.rpc.h1.support;

import lombok.Getter;

/**
 * Internal HTTP/2 header names.
 */
@Getter
public enum VirtueHttpHeaderNames {

    VIRTUE_URL("virtue-url"),
    VIRTUE_NAME("virtue-name");

    private final CharSequence name;

    VirtueHttpHeaderNames(CharSequence name) {
        this.name = name;
    }

}
