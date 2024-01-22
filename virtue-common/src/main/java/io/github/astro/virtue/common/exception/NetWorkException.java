package io.github.astro.virtue.common.exception;

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
