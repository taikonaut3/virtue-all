package io.virtue.rpc.listener;

import io.virtue.common.executor.RpcThreadPool;
import io.virtue.event.EventListener;
import io.virtue.rpc.event.SendMessageEvent;

import java.util.concurrent.ExecutorService;

/**
 * Send Message EventListener.
 */
public class SendMessageEventListener implements EventListener<SendMessageEvent> {

    private final ExecutorService executor = RpcThreadPool.defaultCPUExecutor("message-sender");

    @Override
    public void onEvent(SendMessageEvent event) {
        if (!executor.isShutdown()) {
            executor.execute(event.source());
        } else {
            event.source().run();
        }
    }
}
