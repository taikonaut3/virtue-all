package io.virtue.transport.netty.http;

import io.virtue.common.extension.spi.Extension;
import io.virtue.common.url.URL;
import io.virtue.transport.http.HttpTransporter;
import io.virtue.transport.http.HttpVersion;
import io.virtue.transport.http.h1.HttpRequest;
import io.virtue.transport.http.h1.HttpResponse;
import io.virtue.transport.netty.NettyTransporter;

import java.util.Map;

import static io.virtue.common.constant.Components.Transport.NETTY;

/**
 * HttpTransporter base on netty.
 */
@Extension(NETTY)
public class NettyHttpTransporter extends NettyTransporter implements HttpTransporter {

    @Override
    public HttpRequest newRequest(HttpVersion version, URL url, Map<CharSequence, CharSequence> headers, byte[] data) {
        return new NettyHttpRequest(version, url, headers, data);
    }

    @Override
    public HttpResponse newResponse(HttpVersion version, URL url, int statusCode, Map<CharSequence, CharSequence> headers, byte[] data) {
        return new NettyHttpResponse(version, url, statusCode, headers, data);
    }
}
