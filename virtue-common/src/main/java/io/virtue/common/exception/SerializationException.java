package io.virtue.common.exception;

/**
 * Serialization Exception.
 */
public class SerializationException extends CommonException {

    public SerializationException(String msg, Throwable e) {
        super(msg, e);
    }

    public SerializationException(String msg) {
        super(msg);
    }

    public SerializationException(Throwable msg) {
        super(msg);
    }

}
