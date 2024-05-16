package io.virtue.transport.netty.http.h1.server;

import io.virtue.common.exception.BindException;
import io.virtue.common.url.URL;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.netty.server.NettyServer;

/**
 * Http Server.
 */
public class HttpServer extends NettyServer {

    public HttpServer(URL url, ChannelHandler handler, Codec codec) throws BindException {
        super(url, handler, codec);
    }
}
