package io.github.astro;

import io.github.astro.rpc.virtue.config.VirtueCall;
import io.github.astro.rpc.virtue.envelope.VirtueResponse;
import io.github.astro.virtue.config.annotation.Config;
import io.github.astro.virtue.config.annotation.Options;
import io.github.astro.virtue.config.annotation.RemoteCaller;
import io.github.astro.virtue.transport.Response;

import java.util.concurrent.CompletableFuture;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/5 18:58
 */
@RemoteCaller("provider")
public interface Consumer {

    @VirtueCall(service = "345", callMethod = "hello", config = @Config(filters = {"filter2", "test1"}))
    String hello(String world);

    @VirtueCall(service = "345", callMethod = "hello"
            , config = @Config(filters = "test")
            , options = @Options(async = true))
    CompletableFuture<String> helloAsync(String world);

    @VirtueCall(service = "345", callMethod = "hello", config = @Config(filters = "test"), options = @Options(async = true))
    CompletableFuture<Response> helloResp(String world);

    @VirtueCall(service = "345", callMethod = "hello", config = @Config(filters = "test"), options = @Options(async = true))
    CompletableFuture<VirtueResponse> helloDynamicRes(String world);
}
