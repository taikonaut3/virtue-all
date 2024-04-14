package io.virtue.event.disruptor;

import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.exception.RpcException;
import io.virtue.common.executor.RpcThreadFactory;
import io.virtue.common.spi.Extension;
import io.virtue.common.url.URL;
import io.virtue.event.AbstractEventDispatcher;
import io.virtue.event.Event;
import io.virtue.event.EventListener;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.virtue.common.constant.Components.EventDispatcher.DISRUPTOR;
import static io.virtue.common.util.StringUtil.simpleClassName;

/**
 * Disruptor based event dispatcher.
 */
@Extension(DISRUPTOR)
public class DisruptorEventDispatcher extends AbstractEventDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(DisruptorEventDispatcher.class);

    private URL url;

    private volatile RingBuffer<EventHolder<?>> ringBuffer;

    /**
     * Inject the configuration by {@link io.virtue.common.spi.LoadedListener}.
     *
     * @param url
     */
    public void url(URL url) {
        this.url = url;
    }

    private RingBuffer<EventHolder<?>> createRingBuffer() {
        int bufferSize = Constant.DEFAULT_BUFFER_SIZE;
        if (url != null) {
            bufferSize = url.getIntParam(Key.BUFFER_SIZE, Constant.DEFAULT_BUFFER_SIZE);
        }
        Disruptor<EventHolder<?>> disruptor = new Disruptor<>(EventHolder::new, bufferSize, new RpcThreadFactory("EventDisruptorHandler"));
        RingBuffer<EventHolder<?>> ringBuffer = disruptor.getRingBuffer();
        disruptor.handleEventsWith(this::handleEvent);
        disruptor.setDefaultExceptionHandler(new ExceptionHandler<>() {
            @Override
            public void handleEventException(Throwable ex, long sequence, EventHolder<?> event) {
                logger.error(simpleClassName(this) + " Handle Event Error", ex);
            }

            @Override
            public void handleOnStartException(Throwable ex) {
                logger.error(simpleClassName(this) + " Start Error", ex);
            }

            @Override
            public void handleOnShutdownException(Throwable ex) {
                logger.error(simpleClassName(this) + " Shutdown Error", ex);
            }
        });
        disruptor.start();
        return ringBuffer;
    }

    @Override
    public <E extends Event<?>> void addListener(Class<E> eventType, EventListener<E> listener) {
        super.addListener(eventType, listener);
        logger.debug("Register Listener[{}] listen Event[{}]", simpleClassName(listener), simpleClassName(eventType));
    }

    @Override
    public <E extends Event<?>> void removeListener(Class<E> eventType, EventListener<E> listener) {
        super.removeListener(eventType, listener);
        logger.debug("Remove Listener[{}] listen Event[{}]", simpleClassName(listener), simpleClassName(eventType));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <E extends Event<?>> void doDispatchEvent(E event) {
        ringBuffer().publishEvent((eventHolder, sequence) -> {
            EventHolder<E> holder = (EventHolder<E>) eventHolder;
            holder.event(event);
        });
        logger.trace("DispatchEvent ({})", simpleClassName(event));
    }

    private RingBuffer<EventHolder<?>> ringBuffer() {
        if (ringBuffer == null) {
            synchronized (this) {
                if (ringBuffer == null) {
                    ringBuffer = createRingBuffer();
                }
            }
        }
        return ringBuffer;
    }

    @SuppressWarnings("unchecked")
    private <E extends Event<?>> void handleEvent(EventHolder<E> holder, long sequence, boolean endOfBatch) {
        E event = holder.event();
        listenerMap.entrySet().stream()
                .filter(entry -> entry.getKey().isAssignableFrom(event.getClass()))
                .forEach(entry -> entry.getValue().forEach(item -> {
                    EventListener<E> listener = (EventListener<E>) item;
                    if (listener.check(event)) {
                        try {
                            listener.onEvent(event);
                            logger.trace("Listener[{}] handle Event ({})", simpleClassName(listener), simpleClassName(event));
                        } catch (Exception e) {
                            logger.error("Handle Failed Event(" + simpleClassName(event) + ") current Listener ", e);
                            throw RpcException.unwrap(e);
                        }
                    }
                }));
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    private static final class EventHolder<E extends Event<?>> {
        private E event;
    }

}

