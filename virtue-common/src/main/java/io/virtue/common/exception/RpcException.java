package io.virtue.common.exception;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.ExecutionException;

/**
 * Rpc Exception.
 */
public class RpcException extends RuntimeException {

    public RpcException(String msg, Throwable e) {
        super(msg, e);
    }

    public RpcException(String msg) {
        super(msg);
    }

    public RpcException(Throwable e) {
        super(e);
    }

    /**
     * Reverse unwrap exception until the root exception is found.
     *
     * @param wrapped
     * @return
     */
    public static RpcException unwrap(Throwable wrapped) {
        Throwable unwrapped = wrapped;

        while (true) {
            switch (unwrapped) {
                case InvocationTargetException e -> unwrapped = e.getTargetException();
                case UndeclaredThrowableException e -> unwrapped = e.getUndeclaredThrowable();
                case ExecutionException e -> unwrapped = e.getCause();
                case RpcException e -> {
                    return e;
                }
                default -> {
                    return new RpcException(unwrapped);
                }
            }
        }
    }

}
