package io.virtue.common.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ThreadExceptionHandler.
 */
public class ThreadExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ThreadExceptionHandler.class);

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logger.error(t.getName() + " occur error", e);
    }

}
