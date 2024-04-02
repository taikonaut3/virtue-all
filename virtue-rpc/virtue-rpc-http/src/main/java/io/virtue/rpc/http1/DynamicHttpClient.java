package io.virtue.rpc.http1;

import io.virtue.common.url.URL;
import io.virtue.rpc.RpcFuture;
import io.virtue.rpc.http1.config.HttpClient;
import io.virtue.rpc.http1.envelope.HttpRequest;

/**
 * @Author WenBo Zhou
 * @Date 2024/2/29 14:52
 */
public class DynamicHttpClient implements HttpClient {

    public DynamicHttpClient(URL url) {

    }

    @Override
    public RpcFuture send(HttpRequest request) {
        return null;
    }
}
