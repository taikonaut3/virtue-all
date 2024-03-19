package io.virtue.rpc.virtue.server;

import io.virtue.common.constant.Key;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.core.support.RpcCallArgs;
import io.virtue.rpc.handler.RequestChannelHandler;
import io.virtue.rpc.protocol.Protocol;
import io.virtue.rpc.protocol.ProtocolParser;
import io.virtue.serialization.Converter;
import io.virtue.serialization.Serializer;
import io.virtue.transport.Request;
import io.virtue.transport.channel.Channel;
import io.virtue.common.constant.Components;

public class VirtueServerConvertChannelHandler extends RequestChannelHandler {

    @Override
    protected void onSuccess(Channel channel, Request request) {
        URL url = request.url();
        Protocol<?,?> protocol = ExtensionLoader.loadService(Protocol.class, url.protocol());
        ProtocolParser protocolParser = protocol.parser();
        RpcCallArgs callArgs = (RpcCallArgs) protocolParser.parseRequestBody(request);
        String serial = url.getParameter(Key.SERIALIZE);
        if (serial.equals(Components.Serialize.JSON) || serial.equals(Components.Serialize.MSGPACK) || serial.equals(Components.Serialize.PROTOBUF)) {
            Converter converter = ExtensionLoader.loadService(Serializer.class, serial);
            Object[] args = converter.convert(callArgs.args(), callArgs.parameterTypes());
            callArgs.args(args);
        }
    }
}
