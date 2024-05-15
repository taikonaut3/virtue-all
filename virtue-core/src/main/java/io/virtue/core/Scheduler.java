package io.virtue.core;

import io.virtue.common.extension.resoruce.Closeable;
import io.virtue.common.extension.spi.Extensible;

import java.util.concurrent.TimeUnit;

import static io.virtue.common.constant.Components.DEFAULT;

/**
 * Used for publishing disposable or periodic tasks.
 */
@Extensible(DEFAULT)
public interface Scheduler extends Closeable {

    /**
     * Publishing disposable task.
     *
     * @param runnable
     * @param delay
     * @param unit
     */
    void addDisposable(Runnable runnable, long delay, TimeUnit unit);

    /**
     * Publishing periodic task.
     *
     * @param runnable
     * @param delay
     * @param interval
     * @param unit
     */
    void addPeriodic(Runnable runnable, long delay, long interval, TimeUnit unit);
}
