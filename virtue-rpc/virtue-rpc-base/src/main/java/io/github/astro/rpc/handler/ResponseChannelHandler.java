package io.github.astro.rpc.handler;

import io.github.astro.rpc.protocol.Protocol;
import io.github.astro.rpc.protocol.ProtocolParser;
import io.github.astro.virtue.common.exception.RpcException;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.transport.Response;
import io.github.astro.virtue.transport.ResponseFuture;
import io.github.astro.virtue.transport.base.ChannelHandlerAdapter;
import io.github.astro.virtue.transport.channel.Channel;

/**
 * Response status check
 */
public class ResponseChannelHandler extends ChannelHandlerAdapter {

    @Override
    public void received(Channel channel, Object message) throws RpcException {
        if (message instanceof Response response) {
            ResponseFuture future = ResponseFuture.getFuture(String.valueOf(response.id()));
            // if timeout the future will is null
            if (future != null) {
                if (response.code() == Response.ERROR) {
                    // throw server handle Exception -> ChannelHandlerExceptionEventListener
                    onError(channel, response);
                } else if (response.code() == Response.SUCCESS) {
                    onSuccess(channel, response);
                }
            }
        }
    }

    protected void onSuccess(Channel channel, Response response) {

    }

    protected void onError(Channel channel, Response response) {
        URL url = response.url();
        Protocol protocol = ExtensionLoader.loadService(Protocol.class, url.protocol());
        ProtocolParser protocolParser = protocol.getParser(url);
        Object body = protocolParser.parseResponseBody(response);
        throw new RpcException(String.valueOf(body));
    }
}
