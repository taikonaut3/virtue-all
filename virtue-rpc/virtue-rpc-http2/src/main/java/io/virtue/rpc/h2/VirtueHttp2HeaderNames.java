package io.virtue.rpc.h2;

/**
 * Internal HTTP/2 header names.
 */
public enum VirtueHttp2HeaderNames {


    VIRTUE_URL("virtue-url"),
    VIRTUE_NAME("virtue-name");



    private final CharSequence name;

    VirtueHttp2HeaderNames(CharSequence name) {
        this.name = name;
    }

    public CharSequence getName() {
        return name;
    }
}
