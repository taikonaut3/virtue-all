package io.github.taikonaut3.virtue.rpc.http1.okhttp;

import io.github.taikonaut3.virtue.rpc.RpcFuture;
import io.github.taikonaut3.virtue.rpc.http1.config.HttpClient;
import io.github.taikonaut3.virtue.rpc.http1.envelope.HttpRequest;

/**
 * @Author WenBo Zhou
 * @Date 2024/2/29 15:02
 */
public class OkHttpClient implements HttpClient {

    private okhttp3.OkHttpClient okHttpClient;


    @Override
    public RpcFuture send(HttpRequest request) {
        return null;
    }
}
