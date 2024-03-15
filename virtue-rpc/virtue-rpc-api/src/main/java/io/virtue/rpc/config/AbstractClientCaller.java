package io.virtue.rpc.config;

import io.virtue.common.constant.Components;
import io.virtue.common.constant.Key;
import io.virtue.common.exception.RpcException;
import io.virtue.common.exception.SourceException;
import io.virtue.common.extension.RpcContext;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.Parameter;
import io.virtue.common.url.URL;
import io.virtue.common.util.CollectionUtil;
import io.virtue.common.util.NetUtil;
import io.virtue.common.util.ReflectUtil;
import io.virtue.common.util.StringUtil;
import io.virtue.config.CallArgs;
import io.virtue.config.ClientCaller;
import io.virtue.config.Invocation;
import io.virtue.config.RemoteCaller;
import io.virtue.config.annotation.Options;
import io.virtue.config.config.ClientConfig;
import io.virtue.config.config.RegistryConfig;
import io.virtue.config.filter.Filter;
import io.virtue.config.filter.FilterScope;
import io.virtue.config.manager.ClientConfigManager;
import io.virtue.config.manager.ConfigManager;
import io.virtue.config.manager.Virtue;
import io.virtue.governance.directory.Directory;
import io.virtue.governance.faulttolerance.FaultTolerance;
import io.virtue.governance.loadbalance.LoadBalance;
import io.virtue.governance.router.Router;
import io.virtue.registry.Registry;
import io.virtue.registry.RegistryFactory;
import io.virtue.rpc.RpcFuture;
import io.virtue.transport.Request;
import io.virtue.transport.client.Client;
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

    protected Type returnType;

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

    protected volatile List<RegistryConfig> registryConfigs;

    protected List<URL> registryConfigUrls;

    protected AbstractClientCaller(Method method, RemoteCaller<?> remoteCaller, String protocol, Class<T> annoType) {
        super(method, remoteCaller, protocol, annoType);
    }

    @Override
    public void init() {
        Options ops = getOptions();
        // check
        checkAsyncReturnType(method);
        checkDirectUrl(ops);
        // set
        router(ops.router());
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

    @Override
    public void start() {
        if (!remoteCaller().lazyDiscover()) {
            if(registryConfigs==null || registryConfigs.isEmpty()){
                logger.warn("Can't find RegistryConfig(s)");
            }else {
                for (URL registryUrl : registryConfigUrls) {
                    RegistryFactory registryFactory = ExtensionLoader.loadService(RegistryFactory.class, registryUrl.protocol());
                    Registry registry = registryFactory.get(registryUrl);
                    registry.discover(url);
                }
            }
        }
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

    @Override
    public void addRegistryConfig(RegistryConfig... configs) {
        if (registryConfigs == null) {
            synchronized (this) {
                if (registryConfigs == null) {
                    registryConfigs = new LinkedList<>();
                    registryConfigUrls = new LinkedList<>();
                }
            }
        }
        CollectionUtil.addToList(this.registryConfigs,
                (registryConfig, config) ->
                        Objects.equals(config.type(), registryConfig.type())
                                && config.host().equals(registryConfig.host())
                                && config.port() == registryConfig.port(),
                config -> {
                    URL configUrl = config.toUrl();
                    configUrl.attribute(Virtue.ATTRIBUTE_KEY).set(virtue);
                    configUrl.addParameter(Key.VIRTUE, virtue.name());
                    registryConfigUrls.add(configUrl);
                }, configs);

    }

    @Override
    public Object call(URL url, CallArgs args) throws RpcException {
        Object result = null;
        try {
            List<Filter> preFilters = FilterScope.PRE.filterScope(filters);
            Invocation invocation = Invocation.create(url, args);
            invocation.revise(() -> doRpcCall(invocation));
            result = filterChain.filter(invocation, preFilters);
        } catch (RpcException e) {
            logger.error("Remote Call Exception " + this, e);
        } finally {
            RpcContext.clear();
        }
        return result;
    }

    @Override
    public Object call(Invocation invocation) throws RpcException {
        String faultToleranceKey = invocation.url().getParameter(Key.FAULT_TOLERANCE, Components.FaultTolerance.FAIL_FAST);
        FaultTolerance faultTolerance = ExtensionLoader.loadService(FaultTolerance.class, faultToleranceKey);
        invocation.revise(() -> {
            RpcFuture future = send(invocation);
            return async() ? future : future.get();
        });
        return faultTolerance.operation(invocation);
    }

    @Override
    public Type returnType() {
            return returnType;
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

    protected Object doRpcCall(Invocation invocation) {
        try {
            URL url = invocation.url();
            ClientCaller<?> caller = (ClientCaller<?>) invocation.callArgs().caller();
            if (StringUtil.isBlank(caller.directUrl())) {
                // Get available service urls
                String directoryKey = url.getParameter(Key.DIRECTORY, Components.DEFAULT);
                Directory directory = ExtensionLoader.loadService(Directory.class, directoryKey);
                URL[] registryConfigUrls = Optional.ofNullable(caller.registryConfigUrls()).stream()
                        .flatMap(Collection::stream)
                        .toArray(URL[]::new);
                List<URL> availableServiceUrls = directory.list(invocation, registryConfigUrls);
                if (availableServiceUrls.isEmpty()) {
                    throw new SourceException("Not found available service!,Path:" + url.path());
                }
                // Router
                Router router = virtue.attribute(Router.ATTRIBUTE_KEY).get();
                if (router == null) {
                    String routerKey = virtue.configManager().applicationConfig().router();
                    router = ExtensionLoader.loadService(Router.class, routerKey);
                }
                List<URL> finalServiceUrls = router.route(invocation, availableServiceUrls);
                // LoadBalance
                String loadBalanceKey = url.getParameter(Key.LOAD_BALANCE, Components.LoadBalance.RANDOM);
                LoadBalance loadBalance = ExtensionLoader.loadService(LoadBalance.class, loadBalanceKey);
                URL selectedServiceUrl = loadBalance.select(invocation, finalServiceUrls);
                url.address(selectedServiceUrl.address()).addParameters(selectedServiceUrl.parameters());
            }
            // Invocation revise
            invocation.revise(() -> call(invocation));
            List<Filter> postFilters = FilterScope.POST.filterScope(filters);
            return filterChain.filter(invocation, postFilters);
        } catch (RpcException e) {
            throw new RpcException(e);
        }
    }

    protected RpcFuture send(Invocation invocation) {
        URL url = invocation.url();
        CallArgs callArgs = invocation.callArgs();
        Client client = getClient(url.address());
        Object message = protocolInstance.createRequest(url, callArgs);
        Request request = new Request(url, message);
        RpcFuture future = new RpcFuture(url, callArgs);
        future.client(client);
        String requestContextStr = RpcContext.requestContext().toString();
        url.addParameter(Key.REQUEST_CONTEXT, requestContextStr);
        // request
        client.send(request);
        return future;
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

    private void checkAsyncReturnType(Method method) {
        returnType = method.getGenericReturnType();
            if (returnType instanceof ParameterizedType parameterizedType) {
                if (parameterizedType.getRawType() == CompletableFuture.class) {
                    returnType = parameterizedType.getActualTypeArguments()[0];
                    async(true);
            }
        }
    }

    private void checkDirectUrl(Options options) {
        String url = options.url();
        if (!StringUtil.isBlank(url)) {
            try {
                NetUtil.toInetSocketAddress(url);
                directUrl(url);
            } catch (IllegalArgumentException e) {
                logger.error("Illegal direct connection address", e);
                throw new IllegalArgumentException(e);
            }
        } else {
            if (remoteCaller().directAddress() != null) {
                directUrl(NetUtil.getAddress(remoteCaller().directAddress()));
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

    protected Options options() {
        return null;
    }
}
