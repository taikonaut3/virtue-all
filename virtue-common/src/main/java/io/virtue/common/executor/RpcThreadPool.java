package io.virtue.common.executor;

import io.virtue.common.constant.Constant;

import java.util.Set;
import java.util.concurrent.*;

/**
 * Rpc ThreadPool Config.
 */
public class RpcThreadPool extends ThreadPoolExecutor {

    private static final Set<ThreadPoolExecutor> EXECUTORS = new CopyOnWriteArraySet<>();

    public RpcThreadPool(int corePoolSize, int maximumPoolSize, String namePrefix) {
        this(corePoolSize, maximumPoolSize,
                Constant.DEFAULT_KEEPALIVE, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(Constant.DEFAULT_CAPACITY),
                new RpcThreadFactory(namePrefix, false),
                new CallerRunsPolicy());
        EXECUTORS.add(this);
    }

    public RpcThreadPool(int corePoolSize, int maximumPoolSize,
                         long keepAliveTime, TimeUnit unit,
                         BlockingQueue<Runnable> workQueue,
                         ThreadFactory threadFactory,
                         RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    /**
     * The default I/O thread pool.
     *
     * @param namePrefix
     * @return
     */
    public static ExecutorService defaultIOExecutor(String namePrefix) {
        return new RpcThreadPool(Constant.DEFAULT_IO_THREADS, Constant.DEFAULT_IO_MAX_THREADS, namePrefix);
    }

    /**
     * The default CPU thread pool.
     *
     * @param namePrefix
     * @return
     */
    public static ExecutorService defaultCPUExecutor(String namePrefix) {
        return new RpcThreadPool(Constant.DEFAULT_CPU_THREADS, Constant.DEFAULT_CPU_MAX_THREADS, namePrefix);
    }

    public static Set<ThreadPoolExecutor> executors() {
        return EXECUTORS;
    }

    public static void clear() {
        executors().forEach(ExecutorService::shutdown);
    }

}
