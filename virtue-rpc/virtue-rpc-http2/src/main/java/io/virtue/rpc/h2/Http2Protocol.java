package io.virtue.rpc.h2;

import io.virtue.common.spi.Extension;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;
import io.virtue.rpc.h2.envelope.Http2Request;
import io.virtue.rpc.h2.envelope.Http2Response;
import io.virtue.rpc.protocol.AbstractProtocol;
import io.virtue.transport.Transporter;
import io.virtue.transport.http.HttpHeaderNames;
import io.virtue.transport.http.h2.Http2Transporter;

import static io.virtue.common.constant.Components.Protocol.*;

/**
 * Http2 Protocol.
 */
@Extension({HTTP2, H2, H2C})
public class Http2Protocol extends AbstractProtocol<Http2Request, Http2Response> {

    public Http2Protocol() {
        super(HTTP2, null, null, new Http2ProtocolParser());
    }

    @Override
    public Http2Request createRequest(Invocation invocation) {
        return new Http2Request(invocation);
    }

    @Override
    public Http2Response createResponse(URL url, Object payload) {
        return new Http2Response(url, payload);
    }

    @Override
    protected Transporter loadTransporter(String transport) {
        return ExtensionLoader.loadExtension(Http2Transporter.class, transport);
    }

    /**
     * Convert Http 2 Request at the application layer to Http 2 Request at the transport layer..
     *
     * @param request
     * @return
     */
    public io.virtue.transport.http.h2.Http2Request convertToTransportRequest(Http2Request request) {
        URL url = request.url();
        byte[] data = HttpUtil.serialize(request.getHeader(HttpHeaderNames.CONTENT_TYPE), request.body());
        return http2Transporter().newRequest(url, request.headers(), data);
    }

    /**
     * Convert Http 2 Response at the transport layer to Http 2 Response at the application layer.
     *
     * @param response
     * @return
     */
    public io.virtue.transport.http.h2.Http2Response convertToTransportResponse(Http2Response response) {
        URL url = response.url();
        byte[] data = HttpUtil.serialize(response.getHeader(HttpHeaderNames.CONTENT_TYPE), response.body());
        return http2Transporter().newResponse(response.statusCode(), url, response.headers(), data);
    }

    /**
     * Get Http2Transporter.
     *
     * @return
     */
    public Http2Transporter http2Transporter() {
        return (Http2Transporter) transporter;
    }
}
