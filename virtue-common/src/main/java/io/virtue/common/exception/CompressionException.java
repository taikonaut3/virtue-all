package io.virtue.common.exception;

/**
 * Compression Exception.
 */
public class CompressionException extends RpcException {

    public CompressionException(String msg, Throwable e) {
        super(msg, e);
    }

    public CompressionException(String msg) {
        super(msg);
    }

    public CompressionException(Throwable e) {
        super(e);
    }
}
