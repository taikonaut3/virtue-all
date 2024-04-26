package io.virtue.rpc.h2;

import lombok.Getter;

/**
 * Internal HTTP/2 header names.
 */
@Getter
public enum VirtueHttp2HeaderNames {


    VIRTUE_URL("virtue-url"),
    VIRTUE_NAME("virtue-name");



    private final CharSequence name;

    VirtueHttp2HeaderNames(CharSequence name) {
        this.name = name;
    }

}
