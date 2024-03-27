package io.virtue.rpc;

import io.virtue.common.constant.Key;
import io.virtue.common.exception.RpcException;
import io.virtue.common.extension.RpcContext;
import io.virtue.common.url.URL;
import io.virtue.common.util.StringUtil;
import io.virtue.core.Invocation;
import io.virtue.transport.Response;
import io.virtue.transport.client.Client;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Accessors(fluent = true)
@Getter
public class RpcFuture extends CompletableFuture<Object> {

    private static final Logger logger = LoggerFactory.getLogger(RpcFuture.class);

    public static final Map<String, RpcFuture> futures = new ConcurrentHashMap<>();

    private final String id;

    private final URL url;

    private final Invocation invocation;

    @Setter
    private Response response;

    @Setter
    private Client client;

    @Setter
    private Consumer<RpcFuture> completeConsumer;

    public RpcFuture(URL url, Invocation invocation) {
        this.url = url;
        this.invocation = invocation;
        this.id = url.getParam(Key.UNIQUE_ID);
        addFuture(id(), this);
        completeOnTimeout(new TimeoutException(), timeout(), TimeUnit.MILLISECONDS);
        whenComplete((resp, ex) -> {
            removeFuture(id());
            if (completeConsumer != null) {
                completeConsumer.accept(this);
            }
        });
        boolean oneway = url.getBooleanParam(Key.ONEWAY);
        if (oneway) {
            complete(null);
        }
    }

    public static void addFuture(String id, RpcFuture future) {
        if (!StringUtil.isBlank(id)) {
            futures.put(id, future);
        }
    }

    public static void removeFuture(String id) {
        futures.remove(id);
    }

    public static RpcFuture getFuture(String id) {
        return futures.get(id);
    }

    /**
     * Returns a null if no value is returned
     *
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Override
    public Object get() {
        try {
            return super.get(timeout(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            if (e instanceof TimeoutException) {
                throw new RpcException("RPC call timeout: " + timeout() + "ms");
            }
            throw RpcException.unwrap(e);
        } finally {
            if (response != null) {
                RpcContext.currentContext().attribute(Response.ATTRIBUTE_KEY).set(response);
                RpcContext.ResponseContext.parse(response.url());
            }
        }
    }

    public int timeout() {
        return url.getIntParam(Key.TIMEOUT);
    }

    public Type returnType() {
        return invocation.returnType();
    }

}
