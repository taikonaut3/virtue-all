package io.github.astro.virtue.rpc.config;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.exception.RpcException;
import io.github.astro.virtue.common.extension.RpcContext;
import io.github.astro.virtue.common.url.Parameter;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.common.util.NetUtil;
import io.github.astro.virtue.common.util.StringUtil;
import io.github.astro.virtue.config.*;
import io.github.astro.virtue.config.annotation.Options;
import io.github.astro.virtue.config.filter.Filter;
import io.github.astro.virtue.config.filter.FilterScope;
import io.github.astro.virtue.rpc.ComplexClientInvoker;
import io.github.astro.virtue.transport.Request;
import io.github.astro.virtue.transport.ResponseFuture;
import io.github.astro.virtue.transport.client.Client;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Setter
@Getter
@Accessors(fluent = true, chain = true)
public abstract class AbstractClientCaller<T extends Annotation> extends AbstractCaller<T> implements ClientCaller<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractClientCaller.class);

    protected String directUrl;

    @Parameter(Key.RETRIES)
    protected int retires;

    @Parameter(Key.ASYNC)
    protected boolean async;

    @Parameter(Key.LOAD_BALANCE)
    protected String loadBalance;

    @Parameter(Key.DIRECTORY)
    protected String directory;

    @Parameter(Key.ROUTER)
    protected String router;

    @Parameter(Key.FAULT_TOLERANCE)
    protected String faultTolerance;

    @Parameter(Key.TIMEOUT)
    protected int timeout;

    @Parameter(Key.ONEWAY)
    protected boolean oneWay;

    @Parameter(Key.MULTIPLEX)
    protected boolean multiplex;

    @Parameter(Key.CLIENT)
    protected String clientConfig;

    protected Map<String, Client> clients = new ConcurrentHashMap<>();

    protected AbstractClientCaller(Method method, CallerContainer container, String protocol, Class<T> annoType) {
        super(method, container, protocol, annoType);
    }

    @Override
    public void init() {
        Options ops = options();
        // check
        checkAsyncReturnType(ops, method);
        checkDirectUrl(ops.url());
        // set
        async(ops.async());
        router(ops.router());
        directUrl(ops.url());
        timeout(ops.timeout());
        retires(ops.retires());
        directory(ops.directory());
        loadBalance(ops.loadBalance());
        faultTolerance(ops.faultTolerance());
        oneWay(returnType().getTypeName().equals("void"));
        multiplex(ops.multiplex());
        clientConfig(ops.client());
        // subclass init
        doInit();
    }

    @Override
    protected void doStart() {
        url = createUrl();
        invoker = new ComplexClientInvoker(this);
    }

    @Override
    public Object call(CallArgs callArgs) throws RpcException {
        Object result = null;
        try {
            List<Filter> preFilters = FilterScope.PRE.filterScope(filters);
            Invocation invocation = Invocation.create(url, callArgs,
                    inv -> ((ComplexClientInvoker) invoker).invoke(inv));
            result = filterChain.filter(invocation, preFilters);
        } catch (RpcException e) {
            logger.error("Remote Call Exception " + this, e);
        } finally {
            RpcContext.getContext().clear();
        }
        return result;
    }

    @Override
    public Object call(Invocation invocation) throws RpcException {
        try {
            List<Filter> postFilters = FilterScope.POST.filterScope(filters);
            Invocation filterInvocation = Invocation.create(invocation.url(), invocation.callArgs(), this::doCall);
            ResponseFuture future = (ResponseFuture) filterChain.filter(filterInvocation, postFilters);
            return async() ? future : future.get();
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }

    protected ResponseFuture doCall(Invocation invocation) {
        URL url = invocation.url();
        CallArgs callArgs = invocation.callArgs();
        Client client = getClient(url);
        Object message = protocolInstance.createRequest(url, callArgs);
        Request request = new Request(url, message);
        // request
        client.send(request);
        return new ResponseFuture(url, callArgs);
    }

    @Override
    public Type returnType() {
        Type returnType = method.getGenericReturnType();
        if (async()) {
            if (returnType instanceof ParameterizedType parameterizedType) {
                if (parameterizedType.getRawType() != CompletableFuture.class) {
                    throw new IllegalArgumentException("Async returnType should be CompletableFuture");
                }
                return parameterizedType.getActualTypeArguments()[0];
            }
            throw new IllegalArgumentException("Async returnType should be CompletableFuture");
        } else {
            return returnType;
        }
    }

    @Override
    public RemoteCaller<?> remoteCaller() {
        return (RemoteCaller<?>) container();
    }

    private Client getClient(URL url) {
        String address = url.getAddress();
        Client client = clients.get(address);
        if (client == null) {
            client = protocolInstance.openClient(url);
            clients.put(address, client);
        }
        if (!client.isActive()) {
            client.connect();
        }
        return client;
    }

    private void checkAsyncReturnType(Options options, Method method) {
        Type returnType = method.getGenericReturnType();
        if (options.async()) {
            if (returnType instanceof ParameterizedType parameterizedType) {
                if (parameterizedType.getRawType() != CompletableFuture.class) {
                    throw new IllegalArgumentException(method.getName() + " Async returnType should be CompletableFuture");
                }
            } else {
                throw new IllegalArgumentException(method.getName() + " Async returnType should be CompletableFuture");
            }
        }
    }

    private void checkDirectUrl(String url) {
        if (!StringUtil.isBlank(url)) {
            try {
                NetUtil.toInetSocketAddress(url);
            } catch (IllegalArgumentException e) {
                logger.error("Illegal direct connection address", e);
                throw new IllegalArgumentException(e);
            }
        }
    }

    protected abstract Options options();

}
