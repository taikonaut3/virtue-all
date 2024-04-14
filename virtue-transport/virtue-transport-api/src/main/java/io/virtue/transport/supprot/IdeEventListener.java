package io.virtue.transport.supprot;

import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.core.config.ClientConfig;
import io.virtue.event.EventListener;
import io.virtue.transport.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Close idle connections according to core {@link ClientConfig#spareCloseTimes} to reduce resource waste.
 */
public class IdeEventListener implements EventListener<IdeEvent> {

    private static final Logger logger = LoggerFactory.getLogger(IdeEventListener.class);

    @Override
    public void onEvent(IdeEvent event) {
        Channel channel = event.source();
        URL url = channel.get(URL.ATTRIBUTE_KEY);
        AtomicInteger readIdleRecord = channel.get(Key.READER_IDLE_TIMES_ATTRIBUTE_KEY);
        AtomicInteger writeIdleRecord = channel.get(Key.WRITE_IDLE_TIMES_ATTRIBUTE_KEY);
        AtomicInteger allIdlerRecord = channel.get(Key.ALL_IDLE_TIMES_ATTRIBUTE_KEY);
        int spareCloseTimes = Constant.DEFAULT_SPARE_CLOSE_TIMES;
        if (url != null) {
            spareCloseTimes = url.getIntParam(Key.SPARE_CLOSE_TIMES, Constant.DEFAULT_SPARE_CLOSE_TIMES);
        }
        logger.debug("Received Event({})", event.getClass().getSimpleName());
        logger.debug("channel:{},readIdleRecord:{},writeIdleRecord:{},allIdlerRecord:{}",
                channel, readIdleRecord, writeIdleRecord, allIdlerRecord);
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
