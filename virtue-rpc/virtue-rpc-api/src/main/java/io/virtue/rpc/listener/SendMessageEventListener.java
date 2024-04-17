package io.virtue.rpc.listener;

import io.virtue.event.EventListener;
import io.virtue.rpc.event.SendMessageEvent;

/**
 * Send Message EventListener.
 */
public class SendMessageEventListener implements EventListener<SendMessageEvent> {

    @Override
    public void onEvent(SendMessageEvent event) {
        event.source().run();
    }
}
