package io.github.taikonaut3;

import org.example.model1.ParentObject;
import org.example.model1.TestGeneric;
import io.virtue.rpc.virtue.envelope.VirtueResponse;
import io.virtue.transport.Response;
import org.example.Message;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @Author WenBo Zhou
 * @Date 2024/4/1 15:55
 */
@Component
public class ConsumerFallBacker implements Consumer {
    @Override
    public void hello() {

    }

    @Override
    public CompletableFuture<List<TestGeneric<String>>> helloGeneric() {
        return null;
    }

    @Override
    public String hello(String world) {
        return null;
    }

    @Override
    public CompletableFuture<String> helloAsync(String world) {
        return null;
    }

    @Override
    public CompletableFuture<Response> helloResp(String world) {
        return null;
    }

    @Override
    public CompletableFuture<VirtueResponse> helloDynamicRes(String world) {
        return null;
    }

    @Override
    public Message exchangeMessage(Message message) {
        Message message1 = new Message();
        message1.setName("fallback");
        message1.setDate(new Date());
        return message1;
    }

    @Override
    public List<ParentObject> list(List<ParentObject> list) {
        return null;
    }

    @Override
    public List<ParentObject> list(List<ParentObject> list1, List<ParentObject> list2) {
        return null;
    }

    @Override
    public List<ParentObject> http2Test(List<ParentObject> list) {
        return null;
    }
}
