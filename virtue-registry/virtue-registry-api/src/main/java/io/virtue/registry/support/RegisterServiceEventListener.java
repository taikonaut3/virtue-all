package io.virtue.registry.support;

import io.virtue.core.Virtue;
import io.virtue.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * RegisterService EventListener.
 */
public class RegisterServiceEventListener implements EventListener<RegisterServiceEvent> {

    private static final Logger logger = LoggerFactory.getLogger(RegisterServiceEventListener.class);

    @Override
    public void onEvent(RegisterServiceEvent event) {
        Virtue virtue = event.virtue();
        // todo custom config?
        virtue.scheduler().addPeriodic(event.source(), 5, 5, TimeUnit.SECONDS);
        String protocol = event.url().protocol();
        String address = event.url().address();
        logger.info("The <{}>{} service register is executed every 5s", protocol, address);
    }
}
