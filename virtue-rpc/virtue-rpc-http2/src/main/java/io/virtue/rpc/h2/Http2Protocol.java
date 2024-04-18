package io.virtue.rpc.h2;

import io.virtue.common.spi.Extension;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;
import io.virtue.rpc.protocol.AbstractProtocol;
import io.virtue.transport.RpcFuture;
import io.virtue.transport.Transporter;
import io.virtue.transport.channel.Channel;
import io.virtue.transport.http.HttpHeaderNames;
import io.virtue.transport.http.h2.Http2Request;
import io.virtue.transport.http.h2.Http2Response;
import io.virtue.transport.http.h2.Http2StreamSender;
import io.virtue.transport.http.h2.Http2Transporter;

import static io.virtue.common.constant.Components.Protocol.*;

/**
 * Http2 Protocol.
 */
@Extension({HTTP2, H2, H2C})
public class Http2Protocol extends AbstractProtocol<Http2Request, Http2Response> {

    private Http2StreamSender streamSender;

    public Http2Protocol() {
        super(HTTP2, new Http2ProtocolParser(), new Http2InvokerFactory());
    }

    @Override
    protected void doSendRequest(RpcFuture future, Http2Request request) {
        streamSender.send(future, request);
    }

    @Override
    protected void doSendResponse(Channel channel, Http2Response response) {
        streamSender.send(channel, response);
    }

    @Override
    public Http2Request createRequest(Invocation invocation) {
        Http2Invocation http2Invocation = (Http2Invocation) invocation;
        byte[] data = HttpUtil.serialize(http2Invocation.getHeader(HttpHeaderNames.CONTENT_TYPE), http2Invocation.body());
        URL requestUrl = http2Invocation.url().replicate();
        requestUrl.params().clear();
        http2Invocation.params().forEach((k, v) -> requestUrl.addParam(k.toString(), v.toString()));
        return http2Transporter().newRequest(requestUrl, http2Invocation.headers(), data);
    }

    @Override
    public Http2Response createResponse(Invocation invocation, Object result) {
        Http2Invocation http2Invocation = (Http2Invocation) invocation;
        Http2Wrapper wrapper = ((Http2Callee) invocation.invoker()).wrapper();
        byte[] data = HttpUtil.serialize(wrapper.headers().get(HttpHeaderNames.CONTENT_TYPE), http2Invocation.body());
        URL requestUrl = http2Invocation.url().replicate();
        requestUrl.params().clear();
        http2Invocation.params().forEach((k, v) -> requestUrl.addParam(k.toString(), v.toString()));
        return http2Transporter().newResponse(200, requestUrl, wrapper.headers(), data);
    }

    @Override
    public Http2Response createResponse(URL url, Throwable e) {
        String errorMessage = "Server exception: " + e.getMessage();
        return http2Transporter().newResponse(500, url, null, errorMessage.getBytes());
    }

    @Override
    protected Transporter loadTransporter(String transport) {
        streamSender = ExtensionLoader.loadExtension(Http2StreamSender.class, transport);
        return ExtensionLoader.loadExtension(Http2Transporter.class, transport);
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
