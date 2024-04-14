package io.virtue.transport;

import io.virtue.common.constant.Key;
import io.virtue.common.exception.RpcException;
import io.virtue.common.extension.RpcContext;
import io.virtue.common.url.URL;
import io.virtue.core.Invocation;
import io.virtue.transport.client.Client;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Base on CompletableFuture.
 */
@Getter
@Accessors(fluent = true)
public class RpcFuture extends CompletableFuture<Object> {

    public static final Map<Long, RpcFuture> futures = new ConcurrentHashMap<>();

    private static final AtomicLong INCREASE = new AtomicLong(0);

    private final long id;

    private final URL url;

    private final Invocation invocation;

    @Setter
    private Response response;

    @Setter
    private Client client;

    public RpcFuture(Invocation invocation) {
        this.url = invocation.url();
        this.invocation = invocation;
        this.id = INCREASE.getAndIncrement();
        addFuture(id(), this);
        whenComplete((resp, ex) -> removeFuture(id()));
        boolean oneway = url.getBooleanParam(Key.ONEWAY);
        if (oneway) {
            complete(null);
        }
    }

    public static void addFuture(long id, RpcFuture future) {
        futures.put(id, future);
    }

    public static void removeFuture(long id) {
        futures.remove(id);
    }

    public static RpcFuture getFuture(long id) {
        return futures.get(id);
    }

    /**
     * Returns a null if no value is returned.
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
                throw new RpcException("RPC call timeout: " + timeout() + "ms", e);
            }
            throw RpcException.unwrap(e);
        } finally {
            if (response != null) {
                RpcContext.currentContext().set(Response.ATTRIBUTE_KEY, response);
                RpcContext.ResponseContext.parse(response.url());
            }
        }
    }

    /**
     * Rpc timeout.
     *
     * @return
     */
    public int timeout() {
        return url.getIntParam(Key.TIMEOUT);
    }

    /**
     * Method return type.
     *
     * @return
     */
    public Type returnType() {
        return invocation.returnType();
    }

}
