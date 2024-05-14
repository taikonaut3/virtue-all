package io.virtue.rpc.h2;

import io.virtue.common.extension.spi.Extension;
import io.virtue.core.Callee;
import io.virtue.core.Caller;
import io.virtue.core.RemoteCaller;
import io.virtue.core.RemoteService;
import io.virtue.rpc.h1.support.AbstractHttpProtocol;
import io.virtue.transport.http.HttpVersion;

import java.lang.reflect.Method;

import static io.virtue.common.constant.Components.Protocol.H2;
import static io.virtue.common.constant.Components.Protocol.H2C;

/**
 * Http2 Protocol.
 */
@Extension({H2, H2C})
public class Http2Protocol extends AbstractHttpProtocol {

    public Http2Protocol() {
        super(H2, HttpVersion.HTTP_2_0);
    }

    @Override
    public Callee<?> createCallee(Method method, RemoteService<?> remoteService) {
        return new Http2Callee(method, remoteService);
    }

    @Override
    public Caller<?> createCaller(Method method, RemoteCaller<?> remoteCaller) {
        return new Http2Caller(method, remoteCaller);
    }
}
