package io.virtue.rpc.h1;

import io.virtue.common.extension.spi.Extension;
import io.virtue.core.Callee;
import io.virtue.core.Caller;
import io.virtue.core.RemoteCaller;
import io.virtue.core.RemoteService;
import io.virtue.rpc.h1.support.AbstractHttpProtocol;
import io.virtue.transport.http.HttpVersion;

import java.lang.reflect.Method;

import static io.virtue.common.constant.Components.Protocol.HTTP;
import static io.virtue.common.constant.Components.Protocol.HTTPS;

/**
 * Http protocol.
 */
@Extension({HTTP, HTTPS})
public class HttpProtocol extends AbstractHttpProtocol {
    public HttpProtocol() {
        super(HTTP, HttpVersion.HTTP_1_1);
    }

    @Override
    public Callee<?> createCallee(Method method, RemoteService<?> remoteService) {
        return new HttpCallee(method, remoteService);
    }

    @Override
    public Caller<?> createCaller(Method method, RemoteCaller<?> remoteCaller) {
        return new HttpCaller(method, remoteCaller);
    }

}
