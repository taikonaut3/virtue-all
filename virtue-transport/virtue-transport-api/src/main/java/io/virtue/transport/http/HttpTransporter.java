package io.virtue.transport.http;

import io.virtue.common.extension.spi.Extensible;
import io.virtue.common.url.URL;
import io.virtue.transport.Transporter;
import io.virtue.transport.http.h1.HttpRequest;
import io.virtue.transport.http.h1.HttpResponse;

import java.util.Map;

import static io.virtue.common.constant.Components.Transport.NETTY;

/**
 * Http2 transporter.
 */
@Extensible(NETTY)
public interface HttpTransporter extends Transporter {

    /**
     * Create a new full http request.
     *
     * @param version
     * @param url
     * @param headers
     * @param data
     * @return
     */
    HttpRequest newRequest(HttpVersion version, URL url, Map<CharSequence, CharSequence> headers, byte[] data);

    /**
     * Create a new full http response.
     *
     * @param version
     * @param url
     * @param statusCode
     * @param headers
     * @param data
     * @return
     */
    HttpResponse newResponse(HttpVersion version, URL url, int statusCode, Map<CharSequence, CharSequence> headers, byte[] data);

}
