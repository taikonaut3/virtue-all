package io.virtue.rpc.virtue.client;

import io.virtue.common.constant.Key;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.rpc.RpcFuture;
import io.virtue.rpc.handler.ResponseChannelHandler;
import io.virtue.rpc.protocol.Protocol;
import io.virtue.rpc.protocol.ProtocolParser;
import io.virtue.rpc.virtue.envelope.VirtueResponse;
import io.virtue.serialization.Converter;
import io.virtue.serialization.Serializer;
import io.virtue.transport.Response;
import io.virtue.transport.channel.Channel;
import io.virtue.common.constant.Components;

import java.lang.reflect.Type;

public class VirtueClientConvertChannelHandler extends ResponseChannelHandler {

    @Override
    protected void onSuccess(Channel channel, Response response) {
        RpcFuture future = RpcFuture.getFuture(String.valueOf(response.id()));
        URL url = response.url();
        Protocol<?,?> protocol = ExtensionLoader.loadService(Protocol.class, url.protocol());
        ProtocolParser protocolParser = protocol.parser();
        VirtueResponse virtueResponse = (VirtueResponse) response.message();
        String serial = url.getParameter(Key.SERIALIZE);
        if (serial.equals(Components.Serialize.JSON) || serial.equals(Components.Serialize.MSGPACK)) {
            Converter converter = ExtensionLoader.loadService(Serializer.class, serial);
            Type returnType = future.returnType();
            Object body = protocolParser.parseResponseBody(response);
            Object newBody = converter.convert(body, returnType);
            virtueResponse.body(newBody);
        }
    }
}
