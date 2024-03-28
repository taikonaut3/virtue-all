package io.virtue.common.exception;

/**
 * NetWork Exception.
 */
public class NetWorkException extends RpcException {

    public NetWorkException(String msg, Throwable e) {
        super(msg, e);
    }

    public NetWorkException(String msg) {
        super(msg);
    }

    public NetWorkException(Throwable msg) {
        super(msg);
    }

}
