package io.virtue.core.support;

import io.virtue.common.constant.Constant;
import io.virtue.common.spi.Extension;
import io.virtue.core.Scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.virtue.common.constant.Components.DEFAULT;

/**
 * Base on {@link ScheduledExecutorService}.
 */
@Extension(DEFAULT)
public class ScheduledThreadPool implements Scheduler {
    private final ScheduledExecutorService executorService;

    public ScheduledThreadPool() {
        this.executorService = Executors.newScheduledThreadPool(Constant.DEFAULT_CPU_THREADS);
    }

    @Override
    public void addDisposable(Runnable runnable, long delay, TimeUnit unit) {
        executorService.schedule(runnable, delay, unit);
    }

    @Override
    public void addPeriodic(Runnable runnable, long delay, long interval, TimeUnit unit) {
        executorService.scheduleAtFixedRate(runnable, delay, interval, unit);
    }
}