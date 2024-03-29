package io.virtue.rpc.handler;

import io.virtue.common.constant.Key;
import io.virtue.common.exception.RpcException;
import io.virtue.common.url.URL;
import io.virtue.common.util.AssertUtil;
import io.virtue.common.util.NetUtil;
import io.virtue.event.Event;
import io.virtue.rpc.event.ClientHandlerExceptionEvent;
import io.virtue.rpc.event.ServerHandlerExceptionEvent;
import io.virtue.transport.Envelope;
import io.virtue.transport.channel.Channel;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.channel.ChannelHandlerAdapter;
import io.virtue.transport.channel.ChannelHandlerChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DefaultChannelHandlerChain.
 */
public class DefaultChannelHandlerChain extends ChannelHandlerAdapter implements ChannelHandlerChain {

    private static final Logger logger = LoggerFactory.getLogger(DefaultChannelHandlerChain.class);

    private final List<ChannelHandler> channelHandlers = new LinkedList<>();

    private final Map<String, Channel> channels = new ConcurrentHashMap<>();

    public DefaultChannelHandlerChain() {

    }

    public DefaultChannelHandlerChain(ChannelHandler... handlers) {
        AssertUtil.notNull((Object) handlers);
        for (ChannelHandler handler : handlers) {
            addLast(handler);
        }

    }

    @Override
    public ChannelHandlerChain addLast(ChannelHandler channelHandler) {
        channelHandlers.add(channelHandler);
        return this;
    }

    @Override
    public ChannelHandler[] channelHandlers() {
        return channelHandlers.toArray(ChannelHandler[]::new);
    }

    @Override
    public Channel[] getChannels() {
        return channels.values().toArray(Channel[]::new);
    }

    @Override
    public void connected(Channel channel) throws RpcException {
        channels.putIfAbsent(NetUtil.getAddress(channel.remoteAddress()), channel);
        for (ChannelHandler channelHandler : channelHandlers) {
            channelHandler.connected(channel);
        }
        // todo 优化注册中心健康检查时频繁打印日志
        logger.trace("Connected {}", channel);
    }

    @Override
    public void disconnected(Channel channel) throws RpcException {
        channels.remove(NetUtil.getAddress(channel.remoteAddress()));
        for (ChannelHandler channelHandler : channelHandlers) {
            channelHandler.disconnected(channel);
        }
        logger.trace("Disconnected {}", channel);
    }

    @Override
    public void received(Channel channel, Object message) throws RpcException {
        if (message instanceof Envelope envelope) {
            channel.attribute(URL.ATTRIBUTE_KEY).set(envelope.url());
        }
        for (ChannelHandler channelHandler : channelHandlers) {
            channelHandler.received(channel, message);
        }
    }

    @Override
    public void caught(Channel channel, Throwable cause) throws RpcException {
        URL url = channel.attribute(URL.ATTRIBUTE_KEY).get();
        if (url != null) {
            String envelope = url.getParam(Key.ENVELOPE);
            Event<?> exceptionEvent = envelope.equals(Key.REQUEST)
                    ? new ServerHandlerExceptionEvent(channel, cause)
                    : new ClientHandlerExceptionEvent(channel, cause);
            getEventDispatcher(url).dispatchEvent(exceptionEvent);
        }
//        channels.remove(NetUtil.getAddress(channel.remoteAddress()));
        for (ChannelHandler channelHandler : channelHandlers) {
            channelHandler.caught(channel, cause);
        }
        logger.error("Channel has Error", cause);
    }

    @Override
    public void heartBeat(Channel channel, Object event) {
        for (ChannelHandler channelHandler : channelHandlers) {
            channelHandler.heartBeat(channel, event);
        }
    }
}
