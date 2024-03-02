package io.github.taikonaut3.virtue.rpc.http1;

import io.github.taikonaut3.virtue.common.url.URL;
import io.github.taikonaut3.virtue.rpc.RpcFuture;
import io.github.taikonaut3.virtue.rpc.http1.config.HttpClient;
import io.github.taikonaut3.virtue.rpc.http1.envelope.HttpRequest;

/**
 * @Author WenBo Zhou
 * @Date 2024/2/29 14:52
 */
public class DynamicHttpClient implements HttpClient {

    public DynamicHttpClient(URL url){

    }
    @Override
    public RpcFuture send(HttpRequest request) {
        return null;
    }
}
