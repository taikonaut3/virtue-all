package io.virtue.event.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.exception.RpcException;
import io.virtue.common.executor.RpcThreadFactory;
import io.virtue.common.extension.spi.Extension;
import io.virtue.common.extension.spi.LoadedListener;
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
 * Disruptor based support dispatcher.
 */
@Extension(DISRUPTOR)
public class DisruptorEventDispatcher extends AbstractEventDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(DisruptorEventDispatcher.class);

    private URL url;

    private volatile RingBuffer<EventHolder<?>> ringBuffer;

    /**
     * Inject the configuration by {@link LoadedListener}.
     *
     * @param url
     */
    public void url(URL url) {
        this.url = url;
    }

    @Override
    public <E extends Event<?>> void addListener(Class<E> eventType, EventListener<E> listener) {
        super.addListener(eventType, listener);
        if (logger.isDebugEnabled()) {
            logger.debug("Register Listener[{}] listen Event[{}]", simpleClassName(listener), simpleClassName(eventType));
        }
    }

    @Override
    public <E extends Event<?>> void removeListener(Class<E> eventType, EventListener<E> listener) {
        super.removeListener(eventType, listener);
        if (logger.isDebugEnabled()) {
            logger.debug("Remove Listener[{}] listen Event[{}]", simpleClassName(listener), simpleClassName(eventType));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <E extends Event<?>> void doDispatch(E event) {
        ringBuffer().publishEvent((eventHolder, sequence) -> {
            EventHolder<E> holder = (EventHolder<E>) eventHolder;
            holder.event(event);
        });
        if (logger.isTraceEnabled()) {
            logger.trace("dispatch ({})", simpleClassName(event));
        }
    }

    private RingBuffer<EventHolder<?>> createRingBuffer() {
        int bufferSize = Constant.DEFAULT_BUFFER_SIZE;
        int subscribes = Constant.DEFAULT_SUBSCRIBES;
        if (url != null) {
            bufferSize = url.getIntParam(Key.BUFFER_SIZE, Constant.DEFAULT_BUFFER_SIZE);
            subscribes = url.getIntParam(Key.SUBSCRIBES, Constant.DEFAULT_SUBSCRIBES);
        }
        Disruptor<EventHolder<?>> disruptor = new Disruptor<>(EventHolder::new, bufferSize, new RpcThreadFactory("EventDisruptorHandler"));
        RingBuffer<EventHolder<?>> ringBuffer = disruptor.getRingBuffer();
        DisruptorEventHandler<?>[] handlers = new DisruptorEventHandler<?>[subscribes];
        for (int i = 0; i < subscribes; i++) {
            handlers[i] = new DisruptorEventHandler<>();
        }
        disruptor.handleEventsWith(handlers);
        disruptor.setDefaultExceptionHandler(new DisruptorExceptionHandler());
        disruptor.start();
        return ringBuffer;
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

    static final class DisruptorExceptionHandler implements ExceptionHandler<EventHolder<?>> {

        @Override
        public void handleEventException(Throwable ex, long sequence, EventHolder<?> event) {
            logger.error(simpleClassName(this) + " Handle Event(" + simpleClassName(event.event) + ") Exception", ex);
        }

        @Override
        public void handleOnStartException(Throwable ex) {
            logger.error(simpleClassName(this) + " Start Exception", ex);
        }

        @Override
        public void handleOnShutdownException(Throwable ex) {
            logger.error(simpleClassName(this) + " Shutdown Exception", ex);
        }
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    static final class EventHolder<E extends Event<?>> {
        private E event;
    }

    class DisruptorEventHandler<E extends Event<?>> implements EventHandler<EventHolder<E>> {

        @SuppressWarnings("unchecked")
        @Override
        public void onEvent(EventHolder<E> holder, long sequence, boolean endOfBatch) throws Exception {
            E event = holder.event();
            if (event.stopPropagation()) {
                listenerMap.entrySet().stream().filter(entry -> entry.getKey().isAssignableFrom(event.getClass())).forEach(entry -> entry.getValue().forEach(item -> {
                    EventListener<E> listener = (EventListener<E>) item;
                    try {
                        listener.onEvent(event);
                        if (logger.isTraceEnabled()) {
                            logger.trace("Listener[{}] handle Event ({})", simpleClassName(listener), simpleClassName(event));
                        }
                    } catch (Exception e) {
                        logger.error("Handle Failed Event(" + simpleClassName(event) + ") current Listener ", e);
                        throw RpcException.unwrap(e);
                    } finally {
                        event.stopPropagation();
                    }
                }));
            }
        }
    }

}

