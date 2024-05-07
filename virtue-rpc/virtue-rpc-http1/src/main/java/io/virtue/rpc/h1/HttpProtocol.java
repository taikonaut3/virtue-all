package io.virtue.rpc.h1;

import io.virtue.common.extension.spi.Extension;
import io.virtue.core.Callee;
import io.virtue.core.Caller;
import io.virtue.core.RemoteCaller;
import io.virtue.core.RemoteService;
import io.virtue.rpc.h1.support.AbstractHttpProtocol;
import io.virtue.transport.Request;
import io.virtue.transport.Response;
import io.virtue.transport.RpcFuture;
import io.virtue.transport.channel.Channel;
import io.virtue.transport.client.Client;
import io.virtue.transport.http.HttpVersion;
import io.virtue.transport.http.h1.HttpRequest;
import io.virtue.transport.http.h1.HttpResponse;

import java.lang.reflect.Method;

import static io.virtue.common.constant.Components.Protocol.HTTP;

/**
 * Http protocol.
 */
@Extension(HTTP)
public class HttpProtocol extends AbstractHttpProtocol {
    public HttpProtocol() {
        super(HTTP, HttpVersion.HTTP_1_1);
    }

    @Override
    public Callee<?> createCallee(Method method, RemoteService<?> remoteService) {
        return new HttpCallee(method, remoteService);
    }

    @Override
    public Caller<?> createCaller(Method method, RemoteCaller<?> remoteCaller) {
        return new HttpCaller(method, remoteCaller);
    }

    @Override
    protected void doSendRequest(RpcFuture future, HttpRequest httpRequest) {
        Client client = future.client();
        Request request = new Request(future.url(), httpRequest);
        client.send(request);
    }

    @Override
    protected void doSendResponse(Channel channel, HttpResponse httpResponse) {
        Response response = httpResponse.statusCode() == 200
                ? Response.success(httpResponse.url(), httpResponse)
                : Response.error(httpResponse.url(), httpResponse);
        channel.send(response);
    }
}
