package io.virtue.common.exception;

/**
 * Conversion Exception.
 */
public class ConversionException extends RpcException {

    public ConversionException(String msg, Throwable e) {
        super(msg, e);
    }

    public ConversionException(String msg) {
        super(msg);
    }

    public ConversionException(Throwable msg) {
        super(msg);
    }

}
