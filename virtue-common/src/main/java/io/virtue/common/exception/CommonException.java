package io.virtue.common.exception;

/**
 * Common Exception.
 */
public class CommonException extends RuntimeException {


    public CommonException(String msg, Throwable e) {
        super(msg, e);
    }

    public CommonException(String msg) {
        super(msg);
    }

    public CommonException(Throwable e) {
        super(e);
    }
}
