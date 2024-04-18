package io.virtue.rpc.listener;

import io.virtue.common.executor.RpcThreadPool;
import io.virtue.event.EventListener;
import io.virtue.rpc.event.SendMessageEvent;

import java.util.concurrent.Executor;

/**
 * Send Message EventListener.
 */
public class SendMessageEventListener implements EventListener<SendMessageEvent> {

    private final Executor executor = RpcThreadPool.defaultCPUExecutor("SendMessageListener");

    @Override
    public void onEvent(SendMessageEvent event) {
        executor.execute(event.source());
    }
}
