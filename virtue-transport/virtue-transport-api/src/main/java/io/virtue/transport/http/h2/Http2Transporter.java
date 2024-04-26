package io.virtue.transport.http.h2;

import io.virtue.common.spi.Extensible;
import io.virtue.common.url.URL;
import io.virtue.transport.Transporter;

import java.util.Map;

import static io.virtue.common.constant.Components.Transport.NETTY;

/**
 * Http2 transporter.
 */
@Extensible(NETTY)
public interface Http2Transporter extends Transporter {

    /**
     * Create a new http2 request.
     *
     * @param url
     * @param headers
     * @param body
     * @return
     */
    Http2Request newRequest(URL url, Map<CharSequence, CharSequence> headers, byte[] body);

    /**
     * Create a new http2 response.
     *
     * @param statusCode
     * @param url
     * @param headers
     * @param body
     * @return
     */
    Http2Response newResponse(int statusCode, URL url, Map<CharSequence, CharSequence> headers, byte[] body);

}
