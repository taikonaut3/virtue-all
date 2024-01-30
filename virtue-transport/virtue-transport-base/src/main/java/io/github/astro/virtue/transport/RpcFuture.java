package io.github.astro.virtue.transport;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.exception.RpcException;
import io.github.astro.virtue.common.extension.RpcContext;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.common.util.StringUtil;
import io.github.astro.virtue.config.CallArgs;
import io.github.astro.virtue.transport.client.Client;
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

    private final CallArgs callArgs;

    @Setter
    private Response response;

    @Setter
    private Client client;

    @Setter
    private Consumer<RpcFuture> completeConsumer;

    public RpcFuture(URL url, CallArgs data) {
        this.url = url;
        this.callArgs = data;
        this.id = url.getParameter(Key.UNIQUE_ID);
        addFuture(id(), this);
        completeOnTimeout(new TimeoutException("RPC call timeout: " + timeout() + "ms"), timeout(), TimeUnit.MILLISECONDS);
        whenComplete((resp, ex) -> {
            removeFuture(id());
            if (completeConsumer != null) {
                completeConsumer.accept(this);
            }
        });
        boolean oneway = url.getBooleanParameter(Key.ONEWAY);
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
    public Object get() throws InterruptedException, ExecutionException {
        try {
            return super.get(timeout(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RpcException(e);
        } finally {
            RpcContext.getContext().set("response", response);
        }
    }

    public int timeout() {
        return Integer.parseInt(url.getParameter(Key.TIMEOUT));
    }

    public Type returnType() {
        return callArgs.returnType();
    }

}
