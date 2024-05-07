package io.virtue.rpc.h2;

import io.virtue.common.extension.spi.Extension;
import io.virtue.common.extension.spi.ExtensionLoader;
import io.virtue.core.Callee;
import io.virtue.core.Caller;
import io.virtue.core.RemoteCaller;
import io.virtue.core.RemoteService;
import io.virtue.rpc.h1.support.AbstractHttpProtocol;
import io.virtue.transport.RpcFuture;
import io.virtue.transport.Transporter;
import io.virtue.transport.channel.Channel;
import io.virtue.transport.http.HttpVersion;
import io.virtue.transport.http.h1.HttpRequest;
import io.virtue.transport.http.h1.HttpResponse;
import io.virtue.transport.http.h2.Http2StreamSender;

import java.lang.reflect.Method;

import static io.virtue.common.constant.Components.Protocol.*;

/**
 * Http2 Protocol.
 */
@Extension({HTTP2, H2, H2C})
public class Http2Protocol extends AbstractHttpProtocol {

    private Http2StreamSender streamSender;

    public Http2Protocol() {
        super(HTTP2, HttpVersion.HTTP_2_0);
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
    protected Transporter loadTransporter(String transport) {
        Transporter transporter = super.loadTransporter(transport);
        streamSender = ExtensionLoader.loadExtension(Http2StreamSender.class, transport);
        return transporter;
    }

    @Override
    protected void doSendRequest(RpcFuture future, HttpRequest request) {
        streamSender.send(future, request);
    }

    @Override
    protected void doSendResponse(Channel channel, HttpResponse response) {
        streamSender.send(channel, response);
    }
}
