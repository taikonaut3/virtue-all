package io.virtue.rpc.support;

import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.exception.RpcException;
import io.virtue.common.extension.RpcContext;
import io.virtue.common.spi.ExtensionLoader;
import io.virtue.common.url.Parameter;
import io.virtue.common.url.URL;
import io.virtue.common.util.CollectionUtil;
import io.virtue.common.util.NetUtil;
import io.virtue.common.util.ReflectUtil;
import io.virtue.common.util.StringUtil;
import io.virtue.core.CallArgs;
import io.virtue.core.ClientCaller;
import io.virtue.core.Invocation;
import io.virtue.core.RemoteCaller;
import io.virtue.core.annotation.Options;
import io.virtue.core.config.ClientConfig;
import io.virtue.core.config.RegistryConfig;
import io.virtue.core.filter.Filter;
import io.virtue.core.filter.FilterScope;
import io.virtue.core.manager.ClientConfigManager;
import io.virtue.core.manager.ConfigManager;
import io.virtue.core.manager.Virtue;
import io.virtue.governance.discovery.ServiceDiscovery;
import io.virtue.governance.faulttolerance.FaultTolerance;
import io.virtue.governance.loadbalance.LoadBalancer;
import io.virtue.governance.router.Router;
import io.virtue.registry.RegistryFactory;
import io.virtue.registry.RegistryService;
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
import java.util.concurrent.atomic.AtomicInteger;

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

    @Parameter(Key.SERVICE_DISCOVERY)
    protected String serviceDiscovery;

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
        serviceDiscovery(ops.serviceDiscovery());
        loadBalance(ops.loadBalance());
        faultTolerance(ops.faultTolerance());
        oneWay(returnType().getTypeName().equals("void"));
        multiplex(ops.multiplex());
        clientConfig(ops.client());
        // subclass init
        doInit();
        // parse core
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
                    RegistryService registryService = registryFactory.get(registryUrl);
                    registryService.discover(url);
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
        } catch (Exception e) {
            logger.error("Remote Call fail " + this, e);
            throw RpcException.unwrap(e);
        } finally {
            RpcContext.clear();
        }
        return result;
    }

    @Override
    public Object call(Invocation invocation) throws RpcException {
        String faultToleranceName = invocation.url().getParameter(Key.FAULT_TOLERANCE, Constant.DEFAULT_FAULT_TOLERANCE);
        FaultTolerance faultTolerance = ExtensionLoader.loadService(FaultTolerance.class, faultToleranceName);
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
        url.attribute(Key.LAST_CALL_INDEX_ATTRIBUTE_KEY).set(new AtomicInteger(-1));
        return url;
    }

    protected Object doRpcCall(Invocation invocation) {
        URL url = invocation.url();
        ClientCaller<?> caller = (ClientCaller<?>) invocation.callArgs().caller();
        if (StringUtil.isBlank(caller.directUrl())) {
            // ServiceDiscovery
            String serviceDiscoveryName = url.getParameter(Key.SERVICE_DISCOVERY, Constant.DEFAULT_SERVICE_DISCOVERY);
            ServiceDiscovery serviceDiscovery = ExtensionLoader.loadService(ServiceDiscovery.class, serviceDiscoveryName);
            URL[] registryConfigUrls = Optional.ofNullable(caller.registryConfigUrls()).stream()
                    .flatMap(Collection::stream)
                    .toArray(URL[]::new);
            List<URL> availableServiceUrls = serviceDiscovery.discover(invocation, registryConfigUrls);
            // Router
            Router router = virtue.attribute(Router.ATTRIBUTE_KEY).get();
            if (router == null) {
                String routerName = virtue.configManager().applicationConfig().router();
                routerName = StringUtil.isBlank(routerName) ? Constant.DEFAULT_ROUTER : routerName;
                router = ExtensionLoader.loadService(Router.class, routerName);
            }
            List<URL> finalServiceUrls = router.route(invocation, availableServiceUrls);
            // LoadBalance
            String loadBalancerName = url.getParameter(Key.LOAD_BALANCE, Constant.DEFAULT_LOAD_BALANCE);
            LoadBalancer loadBalancer = ExtensionLoader.loadService(LoadBalancer.class, loadBalancerName);
            URL selectedServiceUrl = loadBalancer.choose(invocation, finalServiceUrls);
            url.address(selectedServiceUrl.address()).addParameters(selectedServiceUrl.parameters());
        }
        // Invocation revise
        invocation.revise(() -> call(invocation));
        List<Filter> postFilters = FilterScope.POST.filterScope(filters);
        return filterChain.filter(invocation, postFilters);
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
                throw new IllegalArgumentException("Illegal direct connection address", e);
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
