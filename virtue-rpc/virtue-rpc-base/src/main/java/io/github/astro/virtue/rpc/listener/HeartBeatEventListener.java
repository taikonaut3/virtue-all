package io.github.astro.virtue.rpc.listener;

import io.github.astro.virtue.rpc.event.HeartBeatEvent;
import io.github.astro.virtue.common.constant.Constant;
import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.config.ClientConfig;
import io.github.astro.virtue.event.EventListener;
import io.github.astro.virtue.transport.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Close idle connections according to config
 * {@link ClientConfig#spareCloseTimes}
 * to reduce resource waste
 */
public class HeartBeatEventListener implements EventListener<HeartBeatEvent> {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatEventListener.class);

    @Override
    public void onEvent(HeartBeatEvent event) {
        Channel channel = event.source();
        URL url = (URL) channel.getAttribute(Key.URL);
        AtomicInteger readIdleRecord = (AtomicInteger) channel.getAttribute(Key.READER_IDLE_TIMES);
        AtomicInteger writeIdleRecord = (AtomicInteger) channel.getAttribute(Key.WRITE_IDLE_TIMES);
        AtomicInteger allIdlerRecord = (AtomicInteger) channel.getAttribute(Key.ALL_IDLE_TIMES);
        int spareCloseTimes = Constant.DEFAULT_SPARE_CLOSE_TIMES;
        if (url != null) {
            spareCloseTimes = url.getIntParameter(Key.SPARE_CLOSE_TIMES, Constant.DEFAULT_SPARE_CLOSE_TIMES);
        }
        logger.trace("Received Event({})", event.getClass().getSimpleName());
        logger.trace("readIdleRecord:{},writeIdleRecord:{},allIdlerRecord:{}", readIdleRecord, writeIdleRecord, allIdlerRecord);
        if (allIdlerRecord != null) {
            int allIdleTimes = allIdlerRecord.incrementAndGet();
            if (readIdleRecord != null) {
                int readIdleTimes = readIdleRecord.incrementAndGet();
                if (readIdleTimes > spareCloseTimes && allIdleTimes > spareCloseTimes) {
                    channel.close();
                }
            }
            if (writeIdleRecord != null) {
                int writeIdleTimes = writeIdleRecord.incrementAndGet();
                if (writeIdleTimes > spareCloseTimes && allIdleTimes > spareCloseTimes) {
                    channel.close();
                }
            }
        }
    }

}
