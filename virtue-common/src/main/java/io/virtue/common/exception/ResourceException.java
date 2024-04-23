package io.virtue.common.exception;

/**
 * Get Resources Exception.
 */
public class ResourceException extends RpcException {

    public ResourceException(String msg) {
        super(msg);
    }

    public ResourceException(String msg, Throwable e) {
        super(msg, e);
    }
}
