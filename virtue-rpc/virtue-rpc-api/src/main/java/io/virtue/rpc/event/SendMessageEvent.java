package io.virtue.rpc.event;

import io.virtue.event.AbstractEvent;

/**
 * Send Message Event.
 */
public class SendMessageEvent extends AbstractEvent<Runnable> {

    public SendMessageEvent(Runnable runnable) {
        super(runnable);
    }

}
