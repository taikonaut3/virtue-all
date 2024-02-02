package io.github.astro.virtue.config;

import io.github.astro.virtue.common.spi.ServiceInterface;

import java.util.concurrent.TimeUnit;

import static io.github.astro.virtue.common.constant.Components.DEFAULT;

@ServiceInterface(DEFAULT)
public interface Scheduler {

    void addDisposable(Runnable runnable, long delay, TimeUnit unit);

    void addPeriodic(Runnable runnable, long delay, long interval, TimeUnit unit);
}
