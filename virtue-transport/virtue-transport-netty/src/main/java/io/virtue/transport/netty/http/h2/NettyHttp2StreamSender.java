package io.virtue.transport.netty.http.h2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.DefaultHttp2DataFrame;
import io.netty.handler.codec.http2.DefaultHttp2HeadersFrame;
import io.netty.handler.codec.http2.Http2Frame;
import io.netty.handler.codec.http2.Http2Headers;
import io.virtue.common.extension.spi.Extension;
import io.virtue.common.url.URL;
import io.virtue.transport.RpcFuture;
import io.virtue.transport.channel.Channel;
import io.virtue.transport.http.HttpMethod;
import io.virtue.transport.http.h1.HttpRequest;
import io.virtue.transport.http.h1.HttpResponse;
import io.virtue.transport.http.h2.Http2StreamSender;
import io.virtue.transport.netty.http.NettyHttpRequest;
import io.virtue.transport.netty.http.NettyHttpResponse;
import io.virtue.transport.netty.http.h2.client.Http2Client;

import static io.virtue.common.constant.Components.Transport.NETTY;
import static io.virtue.transport.util.TransportUtil.getScheme;

/**
 * Http2StreamSender based on netty.
 */
@Extension(NETTY)
public class NettyHttp2StreamSender implements Http2StreamSender {

    @Override
    public void send(RpcFuture future, HttpRequest request) {
        Http2Client http2Client = (Http2Client) future.client();
        if (request instanceof NettyHttpRequest httpRequest) {
            URL url = httpRequest.url();
            Http2Headers headers = ((NettyHttp2Headers) httpRequest.headers()).headers();
            headers.scheme(getScheme(url));
            headers.method(HttpMethod.getOf(url).name());
            headers.path(url.pathAndParams());
            ByteBuf data = Unpooled.wrappedBuffer(httpRequest.data());
            http2Client.send(future, toHttp2Frames(headers, data));
        }
    }

    @Override
    public void send(Channel channel, HttpResponse response) {
        if (response instanceof NettyHttpResponse httpResponse) {
            Http2Headers headers = ((NettyHttp2Headers) httpResponse.headers()).headers();
            ByteBuf data = Unpooled.wrappedBuffer(httpResponse.data());
            headers.status(HttpResponseStatus.valueOf(response.statusCode()).codeAsText());
            Http2Frame[] http2Frames = toHttp2Frames(headers, data);
            for (Http2Frame http2Frame : http2Frames) {
                channel.send(http2Frame);
            }
        }
    }

    private Http2Frame[] toHttp2Frames(Http2Headers headers, ByteBuf data) {
        boolean headersEndStream = !data.isReadable();
        Http2Frame headersFrame = new DefaultHttp2HeadersFrame(headers, headersEndStream);
        Http2Frame dataFrame = headersEndStream ? null : new DefaultHttp2DataFrame(data, true);
        return headersEndStream ? new Http2Frame[]{headersFrame} : new Http2Frame[]{headersFrame, dataFrame};
    }

}
