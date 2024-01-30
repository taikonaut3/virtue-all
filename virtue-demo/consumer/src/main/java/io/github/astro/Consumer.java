package io.github.astro;

import io.github.astro.model.ParentObject;
import io.github.astro.virtue.config.annotation.Config;
import io.github.astro.virtue.config.annotation.Options;
import io.github.astro.virtue.config.annotation.RemoteCaller;
import io.github.astro.virtue.rpc.virtue.config.VirtueCall;
import io.github.astro.virtue.rpc.virtue.envelope.VirtueResponse;
import io.github.astro.virtue.transport.Response;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.github.astro.virtue.common.constant.Components.Serialize.JSON;
import static io.github.astro.virtue.common.constant.Components.Serialize.MSGPACK;

/**
 * @Author WenBo Zhou
 * @Date 2024/1/5 18:58
 */
@RemoteCaller("provider")
public interface Consumer {

    @Config(filters = {"filter2", "filter1"},serialize = JSON)
    @VirtueCall(service = "345", callMethod = "hello")
    String hello(String world);

    @Config(filters = {"filter2", "filter1"},serialize = JSON)
    @Options(async = true)
    @VirtueCall(service = "345", callMethod = "hello")
    CompletableFuture<String> helloAsync(String world);

    @VirtueCall(service = "345", callMethod = "hello", config = @Config(filters = "test"), options = @Options(async = true))
    CompletableFuture<Response> helloResp(String world);

    @VirtueCall(service = "345", callMethod = "hello", config = @Config(filters = "test"), options = @Options(async = true))
    CompletableFuture<VirtueResponse> helloDynamicRes(String world);

    @VirtueCall(service = "345",callMethod = "list",config = @Config(serialize = MSGPACK))
    List<ParentObject> list(List<ParentObject> list);
}
