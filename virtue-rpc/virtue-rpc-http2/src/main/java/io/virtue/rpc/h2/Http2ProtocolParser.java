package io.virtue.rpc.h2;

import io.virtue.core.Callee;
import io.virtue.core.Caller;
import io.virtue.core.Invocation;
import io.virtue.core.support.TransferableInvocation;
import io.virtue.rpc.protocol.AbstractProtocolParser;
import io.virtue.serialization.Serializer;
import io.virtue.transport.Request;
import io.virtue.transport.Response;
import io.virtue.transport.http.HttpHeaderNames;
import io.virtue.transport.http.h2.Http2Request;
import io.virtue.transport.http.h2.Http2Response;

import java.lang.reflect.Parameter;

/**
 * Http2 ProtocolParser.
 */
public class Http2ProtocolParser extends AbstractProtocolParser<Http2Request, Http2Response> {

    @Override
    protected Invocation parseToInvocation(Request request, Http2Request http2Request, Callee<?> callee) {
        String contentType = http2Request.headers().get(HttpHeaderNames.CONTENT_TYPE).toString();
        Serializer serializer = HttpUtil.getSerializer(contentType);
        Parameter parameter = HttpUtil.findBodyParameter(callee);
        Object[] args = new Object[0];
        byte[] data = http2Request.data();
        if (parameter != null && data != null && data.length > 0) {
            Object body = serializer.deserialize(data, parameter.getType());
            body = serializer.convert(body, parameter.getParameterizedType());
            args = new Object[]{body};
        }
        var h2Request = new io.virtue.rpc.h2.envelope.Http2Request(http2Request);
        request.message(h2Request);
        return new TransferableInvocation(request.url(), callee, args);
    }

    @Override
    protected Object parseToReturnObject(Response response, Http2Response http2Response, Caller<?> caller) {
        byte[] data = http2Response.data();
        if (!(caller.returnClass() == Void.class)) {
            if (data != null && data.length > 0) {
                String contentType = http2Response.headers().get(HttpHeaderNames.CONTENT_TYPE).toString();
                Serializer serializer = HttpUtil.getSerializer(contentType);
                Object body = serializer.deserialize(data, caller.returnClass());
                var h2Response = new io.virtue.rpc.h2.envelope.Http2Response(http2Response);
                response.message(h2Response);
                return serializer.convert(body, caller.returnType());
            }
        }
        return null;
    }
}
