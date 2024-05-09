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
package io.virtue.transport.netty.http.h2.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.handler.codec.http2.Http2StreamChannel;
import io.netty.handler.codec.http2.Http2StreamFrame;
import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;
import io.virtue.transport.Response;
import io.virtue.transport.RpcFuture;
import io.virtue.transport.http.HttpVersion;
import io.virtue.transport.netty.http.NettyHttpResponse;
import io.virtue.transport.netty.http.h2.NettyHttp2Stream;

import static io.netty.channel.ChannelHandler.Sharable;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.virtue.transport.netty.http.h2.Util.currentStream;
import static io.virtue.transport.netty.http.h2.Util.removeCurrentStream;

/**
 * Handles HTTP/2 stream frame responses. This is a useful approach if you specifically want to check
 * the main HTTP/2 response DATA/HEADERs, but in this example it's used purely to see whether
 * our request (for a specific stream id) has had a final response (for that same stream id).
 */
@Sharable
public final class Http2ClientHandler extends SimpleChannelInboundHandler<Http2StreamFrame> {

    private final Http2Client http2Client;
    private final URL url;
    private final ChannelHandler handler;

    Http2ClientHandler(Http2Client http2Client, URL url, ChannelHandler handler) {
        this.http2Client = http2Client;
        this.url = url;
        this.handler = handler;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().addLast(handler);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Http2StreamFrame msg) throws Exception {
        if (msg instanceof Http2HeadersFrame) {
            onHeadersRead(ctx, (Http2HeadersFrame) msg);
        } else if (msg instanceof Http2DataFrame) {
            onDataRead(ctx, (Http2DataFrame) msg);
        }
    }

    /**
     * handle headers frame.
     *
     * @param ctx
     * @param headersFrame
     * @throws Exception
     */
    private void onHeadersRead(ChannelHandlerContext ctx, Http2HeadersFrame headersFrame) throws Exception {
        var currentStream = currentStream(ctx, headersFrame.stream().id(), url);
        currentStream.parseHeaderFrame(headersFrame);
        if (currentStream.endStream()) {
            removeCurrentStream(ctx, currentStream);
            fireChannelRead(ctx, currentStream);
        }
    }

    /**
     * Handle data frame.
     *
     * @param ctx
     * @param dataFrame
     * @throws Exception
     */
    private void onDataRead(ChannelHandlerContext ctx, Http2DataFrame dataFrame) throws Exception {
        var currentStream = currentStream(ctx, dataFrame.stream().id(), url);
        // todo There appears to be a copy of the data twice?
        currentStream.parseDataFrame(dataFrame);
        if (currentStream.endStream()) {
            removeCurrentStream(ctx, currentStream);
            fireChannelRead(ctx, currentStream);
        }
    }

    private void fireChannelRead(ChannelHandlerContext ctx, NettyHttp2Stream message) {
        URL url = message.url();
        RpcFuture rpcFuture = http2Client.getRpcFuture((Http2StreamChannel) ctx.channel());
        url.addParam(Key.UNIQUE_ID, String.valueOf(rpcFuture.id()));
        HttpResponseStatus responseStatus = HttpResponseStatus.parseLine(message.http2Headers().status());
        NettyHttpResponse httpResponse =
                new NettyHttpResponse(HttpVersion.HTTP_2_0, message.url(), responseStatus, message.headers(), message.data());
        Response response;
        if (responseStatus == OK) {
            response = Response.success(url, httpResponse);
        } else {
            response = Response.error(url, httpResponse);
        }
        ctx.fireChannelRead(response);
    }
}
