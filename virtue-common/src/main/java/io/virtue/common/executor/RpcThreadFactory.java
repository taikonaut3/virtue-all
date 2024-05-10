package io.virtue.common.executor;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;

/**
 * Rpc ThreadFactory.
 */
public class RpcThreadFactory implements ThreadFactory {

    private final ThreadExceptionHandler threadExceptionHandler = new ThreadExceptionHandler();

    private final String namePrefix;

    private final boolean isDaemon;

    public RpcThreadFactory(String namePrefix, boolean isDaemon) {
        this.namePrefix = namePrefix;
        this.isDaemon = isDaemon;
    }

    public RpcThreadFactory(String namePrefix) {
        this.namePrefix = namePrefix;
        this.isDaemon = false;
    }

    @Override
    public Thread newThread(Runnable r) {
        Objects.requireNonNull(r);
        Thread t = new Thread(r);
        t.setName(namePrefix + "-" + t.threadId());
        t.setUncaughtExceptionHandler(threadExceptionHandler);
        t.setDaemon(isDaemon);
        return t;
    }
}
