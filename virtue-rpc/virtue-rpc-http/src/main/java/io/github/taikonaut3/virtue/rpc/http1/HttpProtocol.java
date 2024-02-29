package io.github.taikonaut3.virtue.rpc.http1;

import io.github.taikonaut3.virtue.common.constant.Components;
import io.github.taikonaut3.virtue.common.url.URL;
import io.github.taikonaut3.virtue.config.CallArgs;
import io.github.taikonaut3.virtue.rpc.http1.envelope.HttpRequest;
import io.github.taikonaut3.virtue.rpc.protocol.Protocol;
import io.github.taikonaut3.virtue.rpc.protocol.ProtocolParser;
import io.github.taikonaut3.virtue.transport.client.Client;
import io.github.taikonaut3.virtue.transport.codec.Codec;
import io.github.taikonaut3.virtue.transport.server.Server;

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
    public HttpRequest createRequest(URL url, CallArgs args) {
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
