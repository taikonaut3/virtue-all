package io.virtue.common.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rpc ThreadFactory.
 */
public class RpcThreadFactory implements ThreadFactory {

    private final ThreadExceptionHandler threadExceptionHandler = new ThreadExceptionHandler();

    private final AtomicInteger num = new AtomicInteger(0);

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
        Thread t = new Thread(r);
        t.setName(namePrefix + "-" + num.incrementAndGet());
        t.setUncaughtExceptionHandler(threadExceptionHandler);
        t.setDaemon(isDaemon);
        return t;
    }
}
