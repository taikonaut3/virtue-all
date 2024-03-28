package io.virtue.common.exception;

/**
 * Codec Exception.
 */
public class CodecException extends NetWorkException {

    public CodecException(String msg, Throwable e) {
        super(msg, e);
    }

    public CodecException(String msg) {
        super(msg);
    }

    public CodecException(Throwable msg) {
        super(msg);
    }

}
