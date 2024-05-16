package io.virtue.rpc.handler;

import io.virtue.common.exception.RpcException;
import io.virtue.common.util.AssertUtil;
import io.virtue.common.util.NetUtil;
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
        if (logger.isTraceEnabled()) {
            logger.trace("Connected {}", channel);
        }
    }

    @Override
    public void disconnected(Channel channel) throws RpcException {
        channels.remove(NetUtil.getAddress(channel.remoteAddress()));
        for (ChannelHandler channelHandler : channelHandlers) {
            channelHandler.disconnected(channel);
        }
        if (logger.isTraceEnabled()) {
            logger.trace("Disconnected {}", channel);
        }
    }

    @Override
    public void received(Channel channel, Object message) throws RpcException {
        for (ChannelHandler channelHandler : channelHandlers) {
            channelHandler.received(channel, message);
        }
    }

    @Override
    public void caught(Channel channel, Throwable cause) throws RpcException {
        for (ChannelHandler channelHandler : channelHandlers) {
            channelHandler.caught(channel, cause);
        }
        logger.error("Channel occur error", cause);
    }

    @Override
    public void heartBeat(Channel channel, Object event) {
        for (ChannelHandler channelHandler : channelHandlers) {
            channelHandler.heartBeat(channel, event);
        }
    }
}
