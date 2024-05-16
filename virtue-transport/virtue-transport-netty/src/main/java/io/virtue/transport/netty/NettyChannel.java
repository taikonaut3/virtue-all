package io.virtue.transport.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.virtue.common.exception.NetWorkException;
import io.virtue.transport.channel.AbstractChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Base on netty channel.
 */
public final class NettyChannel extends AbstractChannel {

    private static final Logger logger = LoggerFactory.getLogger(NettyChannel.class);

    private static final ConcurrentMap<Channel, NettyChannel> CHANNEL_MAP = new ConcurrentHashMap<>();

    private final Channel channel;

    public NettyChannel(InetSocketAddress localAddress, InetSocketAddress remoteAddress, Channel channel) {
        super(localAddress, remoteAddress);
        this.channel = channel;
    }

    public static NettyChannel getChannel(Channel channel) {
        NettyChannel nettyChannel = CHANNEL_MAP.get(channel);
        if (nettyChannel == null) {
            InetSocketAddress localAddress = (InetSocketAddress) channel.localAddress();
            InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
            nettyChannel = new NettyChannel(localAddress, remoteAddress, channel);
            CHANNEL_MAP.put(channel, nettyChannel);
        }
        return nettyChannel;
    }

    public static void removeChannel(Channel channel) {
        if (channel != null) {
            if (channel.isActive()) {
                channel.close();
            }
            CHANNEL_MAP.remove(channel);
        }
    }

    @Override
    public void doClose() throws NetWorkException {
        try {
            ChannelFuture channelFuture = channel.close();
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    if (CHANNEL_MAP.containsKey(channel)) {
                        removeChannel(channel);
                        logger.debug("{} closed", this);
                    }
                } else {
                    logger.error(this + " closure failed", future.cause());
                }
            });
        } catch (Throwable e) {
            throw new NetWorkException(e);
        }

    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }

    @Override
    public void send(Object message) throws NetWorkException {
        if (isActive()) {
            channel.writeAndFlush(message);
        } else {
            logger.warn("Current channel: {} is closed", this);
        }
    }
}
