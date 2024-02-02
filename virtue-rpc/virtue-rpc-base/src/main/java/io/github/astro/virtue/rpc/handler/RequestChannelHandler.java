package io.github.astro.virtue.rpc.handler;

import io.github.astro.virtue.common.exception.RpcException;
import io.github.astro.virtue.common.exception.SourceException;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.RpcCallArgs;
import io.github.astro.virtue.config.ServerCaller;
import io.github.astro.virtue.config.manager.Virtue;
import io.github.astro.virtue.rpc.protocol.Protocol;
import io.github.astro.virtue.rpc.protocol.ProtocolParser;
import io.github.astro.virtue.transport.Request;
import io.github.astro.virtue.transport.base.ChannelHandlerAdapter;
import io.github.astro.virtue.transport.channel.Channel;

public class RequestChannelHandler extends ChannelHandlerAdapter {

    @Override
    public void received(Channel channel, Object message) throws RpcException {
        if (message instanceof Request request) {
            URL url = request.url();
            Virtue virtue = Virtue.get(url);
            Protocol<?, ?> protocol = ExtensionLoader.loadService(Protocol.class, url.protocol());
            ProtocolParser protocolParser = protocol.parser();
            RpcCallArgs callArgs = (RpcCallArgs) protocolParser.parseRequestBody(request);
            ServerCaller<?> serverCaller = virtue.configManager().remoteServiceManager().getServerCaller(url.protocol(), url.path());
            if (serverCaller == null) {
                throw new SourceException("Can't find  ProviderCaller[" + url.path() + "]");
            }
            callArgs.caller(serverCaller);
            callArgs.returnType(serverCaller.method().getGenericReturnType());
            callArgs.parameterTypes(serverCaller.method().getGenericParameterTypes());
            onSuccess(channel, request);
        }
    }

    protected void onSuccess(Channel channel, Request request) {

    }

}
