package io.virtue.rpc.h2;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.virtue.common.spi.Extension;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.core.*;
import io.virtue.rpc.protocol.AbstractProtocol;
import io.virtue.serialization.Serializer;
import io.virtue.transport.Request;
import io.virtue.transport.Response;
import io.virtue.transport.RpcFuture;
import io.virtue.transport.Transporter;
import io.virtue.transport.channel.Channel;
import io.virtue.transport.http.h2.Http2Request;
import io.virtue.transport.http.h2.Http2Response;
import io.virtue.transport.http.h2.Http2StreamSender;
import io.virtue.transport.http.h2.Http2Transporter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static io.virtue.common.constant.Components.Protocol.*;

/**
 * Http2 Protocol.
 */
@Extension({HTTP2, H2, H2C})
public class Http2Protocol extends AbstractProtocol<Http2Request, Http2Response> {

    private Http2StreamSender streamSender;

    public Http2Protocol() {
        super(HTTP2);
    }

    @Override
    public Callee<?> createCallee(Method method, RemoteService<?> remoteService) {
        return new Http2Callee(method, remoteService);
    }

    @Override
    public Caller<?> createCaller(Method method, RemoteCaller<?> remoteCaller) {
        return new Http2Caller(method, remoteCaller);
    }

    @Override
    public Invocation createInvocation(Caller<?> caller, Object[] args) {
        return new Http2Invocation(caller, args);
    }

    @Override
    public Invocation createInvocation(URL url, Callee<?> callee, Object[] args) {
        return new Http2Invocation(url, callee, args);
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
        http2Invocation.headers().put(HttpHeaderNames.HOST, invocation.url().address());
        byte[] data = HttpUtil.serialize(http2Invocation.getHeader(HttpHeaderNames.CONTENT_TYPE), http2Invocation.body());
        URL requestUrl = http2Invocation.url().replicate();
        requestUrl.params().clear();
        http2Invocation.params().forEach((k, v) -> requestUrl.addParam(k.toString(), v.toString()));
        return http2Transporter().newRequest(requestUrl, http2Invocation.headers(), data);
    }

    @Override
    public Http2Response createResponse(Invocation invocation, Object result) {
        Http2Invocation http2Invocation = (Http2Invocation) invocation;
        byte[] data = HttpUtil.serialize(http2Invocation.headers().get(HttpHeaderNames.CONTENT_TYPE), result);
        URL requestUrl = http2Invocation.url().replicate();
        requestUrl.params().clear();
        http2Invocation.params().forEach((k, v) -> requestUrl.addParam(k.toString(), v.toString()));
        return http2Transporter().newResponse(200, requestUrl, http2Invocation.headers(), data);
    }

    @Override
    public Http2Response createResponse(URL url, Throwable e) {
        String errorMessage = "Server exception: " + e.getMessage();
        return http2Transporter().newResponse(500, url, null, errorMessage.getBytes());
    }

    @Override
    protected Object[] parseToInvokeArgs(Request request, Http2Request http2Request, Callee<?> callee) {
        String contentType = http2Request.headers().get(HttpHeaderNames.CONTENT_TYPE).toString();
        Serializer serializer = HttpUtil.getSerializer(contentType);
        Parameter parameter = HttpUtil.findBodyParameter(callee);
        Object[] args = new Object[1];
        byte[] data = http2Request.data();
        if (parameter != null && data != null && data.length > 0) {
            Object body = serializer.deserialize(data, parameter.getType());
            body = serializer.convert(body, parameter.getParameterizedType());
            args = new Object[]{body};
        }
        return args;
    }

    @Override
    protected Object parseToReturnValue(Response response, Http2Response http2Response, Caller<?> caller) {
        byte[] data = http2Response.data();
        if (!(caller.returnClass() == Void.class)) {
            if (data != null && data.length > 0) {
                String contentType = http2Response.headers().get(HttpHeaderNames.CONTENT_TYPE).toString();
                Serializer serializer = HttpUtil.getSerializer(contentType);
                Object body = serializer.deserialize(data, caller.returnClass());
                return serializer.convert(body, caller.returnType());
            }
        }
        return null;
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
