package io.github.astro.virtue.rpc.config;

import io.github.astro.virtue.common.constant.Components;
import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.exception.RpcException;
import io.github.astro.virtue.common.exception.SourceException;
import io.github.astro.virtue.common.extension.RpcContext;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.url.Parameter;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.common.util.CollectionUtil;
import io.github.astro.virtue.common.util.NetUtil;
import io.github.astro.virtue.common.util.ReflectUtil;
import io.github.astro.virtue.common.util.StringUtil;
import io.github.astro.virtue.config.CallArgs;
import io.github.astro.virtue.config.ClientCaller;
import io.github.astro.virtue.config.Invocation;
import io.github.astro.virtue.config.RemoteCaller;
import io.github.astro.virtue.config.annotation.Options;
import io.github.astro.virtue.config.config.ClientConfig;
import io.github.astro.virtue.config.config.RegistryConfig;
import io.github.astro.virtue.config.filter.Filter;
import io.github.astro.virtue.config.filter.FilterScope;
import io.github.astro.virtue.config.manager.ClientConfigManager;
import io.github.astro.virtue.config.manager.ConfigManager;
import io.github.astro.virtue.config.manager.Virtue;
import io.github.astro.virtue.governance.directory.Directory;
import io.github.astro.virtue.governance.faulttolerance.FaultTolerance;
import io.github.astro.virtue.governance.loadbalance.LoadBalance;
import io.github.astro.virtue.governance.router.Router;
import io.github.astro.virtue.rpc.RpcFuture;
import io.github.astro.virtue.transport.Request;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;

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

    @Parameter(Key.LAZY_DISCOVER)
    protected boolean lazyDiscover;

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

    protected volatile List<RegistryConfig> registryConfigs;

    protected AbstractClientCaller(Method method, RemoteCaller<?> remoteCaller, String protocol, Class<T> annoType) {
        super(method, remoteCaller, protocol, annoType);
        if (!lazyDiscover) {
            discoveryUrls(Invocation.create(url, null));
        }
    }

    @Override
    public void init() {
        Options ops = getOptions();
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
        lazyDiscover(ops.lazyDiscover());
        // subclass init
        doInit();
        // parse config
        ConfigManager configManager = virtue.configManager();
        for (RegistryConfig registryConfig : configManager.registryConfigManager().globalConfigs()) {
            addRegistryConfig(registryConfig);
        }
        String[] registryNames = getOptions().registries();
        Optional.ofNullable(registryNames).ifPresent(names -> Arrays.stream(names).map(configManager.registryConfigManager()::get).filter(Objects::nonNull).forEach(this::addRegistryConfig));
        ClientConfig clientConfig = checkAndGetClientConfig();
        url = createUrl(clientConfig.toUrl());
    }

    private Options getOptions() {
        if (method.isAnnotationPresent(Options.class)) {
            return method.getAnnotation(Options.class);
        }
        if (options() != null) {
            return options();
        }
        return ReflectUtil.getDefaultInstance(Options.class);
    }

    public void addRegistryConfig(RegistryConfig... configs) {
        if (registryConfigs == null) {
            synchronized (this) {
                if (registryConfigs == null) {
                    registryConfigs = new LinkedList<>();
                }
            }
        }
        CollectionUtil.addToList(this.registryConfigs, (registryConfig, config) -> Objects.equals(config.type(), registryConfig.type()) && config.host().equals(registryConfig.host()) && config.port() == registryConfig.port(), configs);
    }

    @Override
    public Object call(URL url, CallArgs args) throws RpcException {
        Object result = null;
        try {
            List<Filter> preFilters = FilterScope.PRE.filterScope(filters);
            Invocation invocation = Invocation.create(url, args, this::governanceCall);
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
            Invocation filterInvocation = Invocation.create(invocation.url(), invocation.callArgs(), this::directRemoteCall);
            RpcFuture future = (RpcFuture) filterChain.filter(filterInvocation, postFilters);
            return async() ? future : future.get();
        } catch (Exception e) {
            throw new RpcException(e);
        }
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
    public Class<?> returnClass() {
        Class<?> returnType = method.getReturnType();
        if (async()) {
            return (Class<?>) ((ParameterizedType) returnType()).getRawType();
        } else {
            return returnType;
        }
    }

    @Override
    public RemoteCaller<?> remoteCaller() {
        return (RemoteCaller<?>) container();
    }

    @Override
    protected URL createUrl(URL clientUrl) {
        String address = remoteApplication;
        if (!StringUtil.isBlank(directUrl)) {
            address = directUrl;
        }
        URL url = new URL(protocol, address);
        url.replacePaths(pathList());
        url.addParameters(clientUrl.parameters());
        url.addParameters(parameterization());
        return url;
    }

    protected Object governanceCall(Invocation invocation) {
        try {
            URL url = selectURL(invocation);
            url.addParameters(invocation.url().parameters());
            invocation.url().address(url.address());
            String faultToleranceKey = url.getParameter(Key.FAULT_TOLERANCE, Components.FaultTolerance.FAIL_FAST);
            FaultTolerance faultTolerance = ExtensionLoader.loadService(FaultTolerance.class, faultToleranceKey);
            return faultTolerance.operation(invocation.revise(this::call));
        } catch (RpcException e) {
            throw new RpcException(e);
        }
    }

    protected RpcFuture directRemoteCall(Invocation invocation) {
        URL url = invocation.url();
        CallArgs callArgs = invocation.callArgs();
        Client client = getClient(url.address());
        Object message = protocolInstance.createRequest(url, callArgs);
        Request request = new Request(url, message);
        RpcFuture future = new RpcFuture(url, callArgs);
        future.client(client);
        // request
        client.send(request);
        return future;
    }

    private URL selectURL(Invocation invocation) {
        URL url = invocation.url();
        if (!StringUtil.isBlank(directUrl)) {
            return url;
        }
        List<URL> urls = discoveryUrls(invocation);
        String routerKey = url.getParameter(Key.ROUTER, Components.Router.WEIGHT);
        Router router = ExtensionLoader.loadService(Router.class, routerKey);
        urls = router.route(urls, invocation);
        String loadBalanceKey = url.getParameter(Key.LOAD_BALANCE, Components.LoadBalance.RANDOM);
        LoadBalance loadBalance = ExtensionLoader.loadService(LoadBalance.class, loadBalanceKey);
        return loadBalance.select(invocation, urls);
    }

    private List<URL> discoveryUrls(Invocation invocation) {
        URL url = invocation.url();
        URL[] urls = registryConfigs().stream().map(config -> {
            URL registryUrl = config.toUrl();
            registryUrl.attribute(Virtue.ATTRIBUTE_KEY).set(virtue);
            registryUrl.addParameter(Key.VIRTUE, virtue.name());
            return registryUrl;
        }).toArray(URL[]::new);
        String directoryKey = url.getParameter(Key.DIRECTORY, Components.DEFAULT);
        Directory directory = ExtensionLoader.loadService(Directory.class, directoryKey);
        List<URL> result = directory.list(invocation, urls);
        if (result.isEmpty()) {
            if (lazyDiscover) {
                throw new SourceException("Not found available service!,Path:" + invocation.url().path());
            }
        }
        return result;
    }

    private Client getClient(String address) {
        ClientConfigManager clientConfigManager = virtue.configManager().clientConfigManager();
        ClientConfig clientConfig = clientConfigManager.get(this.clientConfig);
        URL clientUrl = new URL(protocol, address);
        if (clientConfig == null) {
            clientConfig = clientConfigManager.get(protocol);
        }
        clientUrl.addParameters(clientConfig.parameterization());
        clientUrl.addParameter(Key.MULTIPLEX, String.valueOf(multiplex));
        return protocolInstance.openClient(clientUrl);
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

    private ClientConfig checkAndGetClientConfig() {
        ClientConfigManager clientConfigManager = virtue.configManager().clientConfigManager();
        ClientConfig clientConfig = clientConfigManager.get(this.clientConfig);
        if (clientConfig == null) {
            clientConfig = clientConfigManager.get(protocol);
        }
        if (clientConfig == null) {
            clientConfig = new ClientConfig(protocol);
            clientConfigManager.register(clientConfig);
        }
        return clientConfig;
    }

    protected abstract Options options();
}
