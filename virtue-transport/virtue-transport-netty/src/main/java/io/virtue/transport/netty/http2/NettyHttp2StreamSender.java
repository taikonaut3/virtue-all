package io.virtue.transport.netty.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.*;
import io.virtue.common.spi.Extension;
import io.virtue.common.url.URL;
import io.virtue.transport.RpcFuture;
import io.virtue.transport.channel.Channel;
import io.virtue.transport.http.h2.Http2Request;
import io.virtue.transport.http.h2.Http2Response;
import io.virtue.transport.http.h2.Http2StreamSender;
import io.virtue.transport.netty.http2.client.Http2Client;
import io.virtue.transport.netty.http2.envelope.NettyHttp2Headers;
import io.virtue.transport.netty.http2.envelope.NettyHttp2Request;
import io.virtue.transport.netty.http2.envelope.NettyHttp2Response;

import java.util.LinkedList;
import java.util.List;

import static io.virtue.common.constant.Components.Transport.NETTY;
import static io.virtue.transport.util.TransportUtil.getHttpMethod;
import static io.virtue.transport.util.TransportUtil.getScheme;

/**
 * Http2StreamSender based on netty.
 */
@Extension(NETTY)
public class NettyHttp2StreamSender implements Http2StreamSender {

    @Override
    public void send(RpcFuture future, Http2Request request) {
        Http2Client http2Client = (Http2Client) future.client();
        List<Http2Frame> list = new LinkedList<>();
        if (request instanceof NettyHttp2Request http2Request) {
            URL url = http2Request.url();
            Http2Headers headers = ((NettyHttp2Headers) http2Request.headers()).headers();
            headers.scheme(getScheme(url));
            headers.method(getHttpMethod(url).name());
            headers.path(url.pathAndParams());
            ByteBuf data = Unpooled.wrappedBuffer(http2Request.data());
            if (data.isReadable()) {
                headers.add(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(data.readableBytes()));
                Http2HeadersFrame headersFrame = new DefaultHttp2HeadersFrame(headers, false);
                DefaultHttp2DataFrame dataFrame = new DefaultHttp2DataFrame(data, true);
                list.add(headersFrame);
                list.add(dataFrame);
            } else {
                Http2HeadersFrame headersFrame = new DefaultHttp2HeadersFrame(headers, true);
                list.add(headersFrame);
            }
        }
        http2Client.send(future, list.toArray(Http2Frame[]::new));
    }

    @Override
    public void send(Channel channel, Http2Response response) {
        if (response instanceof NettyHttp2Response nettyHttp2Response) {
            Http2Headers headers = ((NettyHttp2Headers) nettyHttp2Response.headers()).headers();
            ByteBuf data = Unpooled.wrappedBuffer(nettyHttp2Response.data());
            headers.status(HttpResponseStatus.valueOf(response.statusCode()).codeAsText());
            if (data.isReadable()) {
                headers.add(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(data.readableBytes()));
                channel.send(new DefaultHttp2HeadersFrame(headers));
                channel.send(new DefaultHttp2DataFrame(data, true));
            } else {
                channel.send(new DefaultHttp2HeadersFrame(headers, true));
            }
        }
    }
}
