package io.virtue.transport.netty.http.h2.server;

import io.virtue.common.exception.BindException;
import io.virtue.common.url.URL;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.netty.server.NettyServer;

/**
 * Http2 Server.
 */
public class Http2Server extends NettyServer {

    public Http2Server(URL url, ChannelHandler handler, Codec codec) throws BindException {
        super(url, handler, codec);
    }
}
