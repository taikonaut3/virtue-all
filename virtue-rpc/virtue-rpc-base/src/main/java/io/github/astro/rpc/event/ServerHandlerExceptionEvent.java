package io.github.astro.rpc.event;

import io.github.astro.virtue.transport.channel.Channel;

/**
 * @Author WenBo Zhou
 * @Date 2023/12/5 15:50
 */
public class ServerHandlerExceptionEvent extends ChannelHandlerExceptionEvent {

    public ServerHandlerExceptionEvent(Channel channel, Throwable cause) {
        super(channel, cause);
    }

}
