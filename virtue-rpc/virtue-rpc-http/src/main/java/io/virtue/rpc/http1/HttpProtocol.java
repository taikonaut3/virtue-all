package io.virtue.rpc.http1;

import io.virtue.common.constant.Components;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;
import io.virtue.rpc.http1.envelope.HttpRequest;
import io.virtue.rpc.protocol.Protocol;
import io.virtue.rpc.protocol.ProtocolParser;
import io.virtue.transport.client.Client;
import io.virtue.transport.codec.Codec;
import io.virtue.transport.server.Server;

/**
 * @Author WenBo Zhou
 * @Date 2024/2/29 14:40
 */
public class HttpProtocol implements Protocol<HttpRequest, Object> {
    @Override
    public String protocol() {
        return Components.Protocol.HTTP1;
    }

    @Override
    public HttpRequest createRequest(Invocation invocation) {
        return null;
    }

    @Override
    public Object createResponse(URL url, Object payload) {
        return null;
    }

    @Override
    public Client openClient(URL url) {
        throw new UnsupportedOperationException("UnSupport openClient");
    }

    @Override
    public Server openServer(URL url) {
        throw new UnsupportedOperationException("UnSupport openServer");
    }

    @Override
    public Codec serverCodec() {
        throw new UnsupportedOperationException("UnSupport serverCodec");
    }

    @Override
    public Codec clientCodec() {
        throw new UnsupportedOperationException("UnSupport clientCodec");
    }

    @Override
    public ProtocolParser parser() {
        return null;
    }
}
