package io.github.taikonaut3;

import io.github.taikonaut3.model.ParentObject;
import io.github.taikonaut3.model.TestGeneric;
import io.virtue.common.constant.Components;
import io.virtue.core.annotation.Config;
import io.virtue.core.annotation.Options;
import io.virtue.core.annotation.RemoteCaller;
import io.virtue.rpc.virtue.config.VirtueCall;
import io.virtue.rpc.virtue.envelope.VirtueResponse;
import io.virtue.transport.Response;
import org.example.Message;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.virtue.common.constant.Components.Serialization.JSON;
import static io.virtue.common.constant.Components.Serialization.MSGPACK;

@RemoteCaller(value = "provider", fallback = ConsumerFallBacker.class)
public interface Consumer {

    @VirtueCall(service = "345", callMethod = "hello")
    void hello();

    @VirtueCall(service = "345", callMethod = "helloGeneric")
    CompletableFuture<List<TestGeneric<String>>> helloGeneric();

    @Config(filters = {"filter2"}, serialization = JSON)
    @VirtueCall(service = "345", callMethod = "hello")
    String hello(String world);

    @Config(filters = {"filter1"}, serialization = JSON)
    @VirtueCall(service = "345", callMethod = "hello")
    CompletableFuture<String> helloAsync(String world);

    @VirtueCall(service = "345", callMethod = "hello")
    @Config(filters = "test")
    CompletableFuture<Response> helloResp(String world);

    @VirtueCall(service = "345", callMethod = "hello")
    @Config(filters = "test")
    CompletableFuture<VirtueResponse> helloDynamicRes(String world);

    @VirtueCall(service = "345", callMethod = "exchangeMessage")
    @Config(filters = {"testFilter", "callerResultFilter"})
    @Options(faultTolerance = Components.FaultTolerance.TIMEOUT_RETRY)
    Message exchangeMessage(Message message);

    @Config(filters = {"filter1", "filter2"}, serialization = MSGPACK)
    @Options(faultTolerance = Components.FaultTolerance.FAIL_RETRY)
    @VirtueCall(service = "345", callMethod = "list")
    List<ParentObject> list(List<ParentObject> list);

    @Config(filters = {"filter1", "filter2"}, serialization = MSGPACK)
    @VirtueCall(service = "345", callMethod = "list2")
    List<ParentObject> list(List<ParentObject> list1, List<ParentObject> list2);

}
