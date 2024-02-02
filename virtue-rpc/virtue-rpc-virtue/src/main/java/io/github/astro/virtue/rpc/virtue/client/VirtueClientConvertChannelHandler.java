package io.github.astro.virtue.rpc.virtue.client;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.rpc.RpcFuture;
import io.github.astro.virtue.rpc.handler.ResponseChannelHandler;
import io.github.astro.virtue.rpc.protocol.Protocol;
import io.github.astro.virtue.rpc.protocol.ProtocolParser;
import io.github.astro.virtue.rpc.virtue.envelope.VirtueResponse;
import io.github.astro.virtue.serialization.Converter;
import io.github.astro.virtue.serialization.Serializer;
import io.github.astro.virtue.transport.Response;
import io.github.astro.virtue.transport.channel.Channel;

import java.lang.reflect.Type;

import static io.github.astro.virtue.common.constant.Components.Serialize.JSON;
import static io.github.astro.virtue.common.constant.Components.Serialize.MSGPACK;

public class VirtueClientConvertChannelHandler extends ResponseChannelHandler {

    @Override
    protected void onSuccess(Channel channel, Response response) {
        RpcFuture future = RpcFuture.getFuture(String.valueOf(response.id()));
        URL url = response.url();
        Protocol<?,?> protocol = ExtensionLoader.loadService(Protocol.class, url.protocol());
        ProtocolParser protocolParser = protocol.parser();
        VirtueResponse virtueResponse = (VirtueResponse) response.message();
        String serial = url.getParameter(Key.SERIALIZE);
        if (serial.equals(JSON) || serial.equals(MSGPACK)) {
            Converter converter = ExtensionLoader.loadService(Serializer.class, serial);
            Type returnType = future.returnType();
            Object body = protocolParser.parseResponseBody(response);
            Object newBody = converter.convert(body, returnType);
            virtueResponse.setBody(newBody);
        }
    }
}
