/*
 * Copyright 2016 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.virtue.transport.netty.http.h2.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http2.Http2FrameCodecBuilder;
import io.netty.handler.codec.http2.Http2MultiplexHandler;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;
import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.transport.netty.NettyIdleStateHandler;
import io.virtue.transport.netty.NettySupport;

/**
 * Negotiates with the browser if HTTP2 or HTTP is going to be used. Once decided, the Netty
 * pipeline is set up with the correct handlers for the selected protocol.
 */
public class Http2OrHttpNegotiationHandler extends ApplicationProtocolNegotiationHandler {

    private final URL url;

    private final NettyIdleStateHandler idleStateHandler;

    private final ChannelHandler handler;

    protected Http2OrHttpNegotiationHandler(URL url, NettyIdleStateHandler idleStateHandler, ChannelHandler handler) {
        super(ApplicationProtocolNames.HTTP_1_1);
        this.url = url;
        this.idleStateHandler = idleStateHandler;
        this.handler = handler;
    }

    @Override
    protected void configurePipeline(ChannelHandlerContext ctx, String protocol) throws Exception {
        if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
            ctx.pipeline()
                    .addLast(idleStateHandler)
                    .addLast(idleStateHandler.handler())
                    .addLast(Http2FrameCodecBuilder.forServer().build())
                    .addLast(new Http2MultiplexHandler(new Http2ServerHandler(url, handler)));
        } else if (ApplicationProtocolNames.HTTP_1_1.equals(protocol)) {
            int maxReceiveSize = url.getIntParam(Key.MAX_RECEIVE_SIZE, Constant.DEFAULT_MAX_MESSAGE_SIZE);
            ChannelHandler[] handlers = NettySupport.createHttpServerHandlers(url, handler);
            ctx.pipeline()
                    .addLast(idleStateHandler)
                    .addLast(idleStateHandler.handler())
                    .addLast(new HttpServerCodec())
                    .addLast(new HttpObjectAggregator(maxReceiveSize))
                    .addLast(handlers);
        } else {
            throw new IllegalStateException("unknown protocol: " + protocol);
        }
    }
}
