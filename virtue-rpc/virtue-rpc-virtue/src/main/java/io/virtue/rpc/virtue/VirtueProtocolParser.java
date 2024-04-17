package io.virtue.rpc.virtue;

import io.virtue.common.constant.Key;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.URL;
import io.virtue.core.Callee;
import io.virtue.core.Caller;
import io.virtue.core.support.TransferableInvocation;
import io.virtue.rpc.protocol.AbstractProtocolParser;
import io.virtue.rpc.virtue.envelope.VirtueRequest;
import io.virtue.rpc.virtue.envelope.VirtueResponse;
import io.virtue.serialization.Serializer;
import io.virtue.transport.Request;
import io.virtue.transport.Response;

import java.lang.reflect.Type;

/**
 * Virtue ProtocolParser.
 */
public class VirtueProtocolParser extends AbstractProtocolParser<VirtueRequest, VirtueResponse> {

    @Override
    protected Object[] parseToInvokeArgs(Request request, VirtueRequest virtueRequest, Callee<?> callee) {
        URL url = request.url();
        TransferableInvocation invocation = (TransferableInvocation) virtueRequest.body();
        String serializationName = url.getParam(Key.SERIALIZATION);
        Serializer serializer = ExtensionLoader.loadExtension(Serializer.class, serializationName);
        return serializer.convert(invocation.args(), callee.method().getGenericParameterTypes());
    }

    @Override
    protected Object parseToReturnValue(Response response, VirtueResponse virtueResponse, Caller<?> caller) {
        URL url = response.url();
        String serializationName = url.getParam(Key.SERIALIZATION);
        Serializer serializer = ExtensionLoader.loadExtension(Serializer.class, serializationName);
        Type returnType = caller.returnType();
        Object body = virtueResponse.body();
        body = serializer.convert(body, returnType);
        virtueResponse.body(body);
        return body;
    }
}
