package io.virtue.transport.netty.http2;

import io.virtue.common.exception.NetWorkException;
import io.virtue.common.spi.Extension;
import io.virtue.common.url.URL;
import io.virtue.transport.channel.ChannelHandler;
import io.virtue.transport.client.Client;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.http.h2.Http2Request;
import io.virtue.transport.http.h2.Http2Response;
import io.virtue.transport.http.h2.Http2Transporter;
import io.virtue.transport.netty.http2.client.Http2Client;
import io.virtue.transport.netty.http2.envelope.NettyHttp2Request;
import io.virtue.transport.netty.http2.envelope.NettyHttp2Response;
import io.virtue.transport.netty.http2.server.Http2Server;
import io.virtue.transport.server.Server;

import java.util.Map;

import static io.virtue.common.constant.Components.Transport.NETTY;

/**
 * Http2Transporter based on netty.
 */
@Extension(value = NETTY, interfaces = Http2Transporter.class)
public class NettyHttp2Transporter implements Http2Transporter {
    @Override
    public Server bind(URL url, ChannelHandler handler, Codec codec) throws NetWorkException {
        return new Http2Server(url, handler, codec);
    }

    @Override
    public Client connect(URL url, ChannelHandler handler, Codec codec) throws NetWorkException {
        return new Http2Client(url, handler, codec);
    }

    @Override
    public Http2Request newRequest(URL url, Map<CharSequence, CharSequence> headers, byte[] body) {
        return new NettyHttp2Request(url, headers, body);
    }

    @Override
    public Http2Response newResponse(int statusCode, URL url, Map<CharSequence, CharSequence> headers, byte[] body) {
        return new NettyHttp2Response(statusCode, url, headers, body);
    }
}
