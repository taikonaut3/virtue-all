package io.github.astro.rpc.virtue.server;

import io.github.astro.rpc.handler.RequestChannelHandler;
import io.github.astro.rpc.protocol.Protocol;
import io.github.astro.rpc.protocol.ProtocolParser;
import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.RpcCallArgs;
import io.github.astro.virtue.serialization.Converter;
import io.github.astro.virtue.serialization.Serializer;
import io.github.astro.virtue.transport.Request;
import io.github.astro.virtue.transport.channel.Channel;

import static io.github.astro.virtue.common.constant.Components.Serialize.JSON;

public class VirtueServerConvertChannelHandler extends RequestChannelHandler {

    @Override
    protected void onSuccess(Channel channel, Request request) {
        URL url = request.url();
        Protocol protocol = ExtensionLoader.loadService(Protocol.class, url.protocol());
        ProtocolParser protocolParser = protocol.getParser(url);
        RpcCallArgs callArgs = (RpcCallArgs) protocolParser.parseRequestBody(request);
        String serial = url.getParameter(Key.SERIALIZE);
        if (serial.equals(JSON)) {
            Converter converter = ExtensionLoader.loadService(Serializer.class, JSON);
            Object[] args = converter.convert(callArgs.args(), callArgs.parameterTypes());
            callArgs.args(args);
        }
    }
}
