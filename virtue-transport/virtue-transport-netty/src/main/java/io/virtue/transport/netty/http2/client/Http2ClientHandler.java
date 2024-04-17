/*
 * Copyright 2020 The Netty Project
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
package io.virtue.transport.netty.http2.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.handler.codec.http2.Http2StreamChannel;
import io.netty.handler.codec.http2.Http2StreamFrame;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.core.Virtue;
import io.virtue.transport.Response;
import io.virtue.transport.RpcFuture;
import io.virtue.transport.netty.http2.envelope.NettyHttp2Headers;
import io.virtue.transport.netty.http2.envelope.NettyHttp2Response;
import io.virtue.transport.netty.http2.envelope.StreamEnvelope;

import static io.netty.channel.ChannelHandler.Sharable;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.virtue.transport.netty.http2.Util.getStreamEnvelope;
import static io.virtue.transport.netty.http2.Util.removeStreamEnvelope;

/**
 * Handles HTTP/2 stream frame responses. This is a useful approach if you specifically want to check
 * the main HTTP/2 response DATA/HEADERs, but in this example it's used purely to see whether
 * our request (for a specific stream id) has had a final response (for that same stream id).
 */
@Sharable
public final class Http2ClientHandler extends SimpleChannelInboundHandler<Http2StreamFrame> {

    private final Http2Client http2Client;

    private final URL url;

    private final Virtue virtue;

    private final ChannelHandler handler;

    public Http2ClientHandler(Http2Client http2Client, URL url, ChannelHandler handler) {
        this.http2Client = http2Client;
        this.url = url;
        this.virtue = Virtue.ofClient(url);
        this.handler = handler;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().addLast(handler);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Http2StreamFrame msg) throws Exception {
        int streamId = msg.stream().id();
        StreamEnvelope streamEnvelope = getStreamEnvelope(ctx, streamId, url, false);
        // isEndStream() is not from a common interface, so we currently must check both
        if (msg instanceof Http2DataFrame dataFrame) {
            ByteBuf byteBuf = dataFrame.content();
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            streamEnvelope.writeData(bytes);
            if (dataFrame.isEndStream()) {
                streamEnvelope.end();
                removeStreamEnvelope(ctx, streamId);
                fireChannelRead(ctx, streamEnvelope);
            }
        } else if (msg instanceof Http2HeadersFrame headersFrame) {
            streamEnvelope.addHeaders(new NettyHttp2Headers(headersFrame.headers()));
            if (headersFrame.isEndStream()) {
                streamEnvelope.end();
                removeStreamEnvelope(ctx, streamId);
                fireChannelRead(ctx, streamEnvelope);
            }
        }
    }

    private void fireChannelRead(ChannelHandlerContext ctx, StreamEnvelope message) {
        URL url = message.url();
        RpcFuture rpcFuture = http2Client.getRpcFuture((Http2StreamChannel) ctx.channel());
        url.addParam(Key.UNIQUE_ID, String.valueOf(rpcFuture.id()));
        NettyHttp2Response http2Response = new NettyHttp2Response(message);
        Response response;
        if (http2Response.statusCode() == OK.code()) {
            response = Response.success(url, http2Response);
        } else {
            response = Response.error(url, http2Response);
        }
        ctx.fireChannelRead(response);
    }
}
