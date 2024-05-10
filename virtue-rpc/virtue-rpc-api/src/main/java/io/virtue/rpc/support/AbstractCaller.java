package io.virtue.rpc.support;

import io.virtue.common.constant.Constant;
import io.virtue.common.constant.Key;
import io.virtue.common.exception.RpcException;
import io.virtue.common.extension.RpcContext;
import io.virtue.common.extension.spi.ExtensionLoader;
import io.virtue.common.url.Parameter;
import io.virtue.common.url.URL;
import io.virtue.common.util.CollectionUtil;
import io.virtue.common.util.NetUtil;
import io.virtue.common.util.ReflectionUtil;
import io.virtue.common.util.StringUtil;
import io.virtue.core.Caller;
import io.virtue.core.Invocation;
import io.virtue.core.RemoteCaller;
import io.virtue.core.Virtue;
import io.virtue.core.annotation.Options;
import io.virtue.core.config.ClientConfig;
import io.virtue.core.config.RegistryConfig;
import io.virtue.core.filter.Filter;
import io.virtue.core.filter.FilterScope;
import io.virtue.core.manager.ClientConfigManager;
import io.virtue.core.manager.ConfigManager;
import io.virtue.governance.discovery.ServiceDiscovery;
import io.virtue.governance.faulttolerance.FaultTolerance;
import io.virtue.governance.loadbalance.LoadBalancer;
import io.virtue.governance.router.Router;
import io.virtue.registry.RegistryFactory;
import io.virtue.registry.RegistryService;
import io.virtue.transport.RpcFuture;
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

/**
 * Abstract ClientCaller.
 *
 * @param <T>
 */
@Setter
@Getter
@Accessors(fluent = true, chain = true)
public abstract class AbstractCaller<T extends Annotation> extends AbstractInvoker<T> implements Caller<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCaller.class);

    protected String directUrl;

    protected Type returnType;

    protected volatile List<RegistryConfig> registryConfigs;

    protected List<URL> registryConfigUrls;

    @Setter
    @Parameter(Key.GROUP)
    protected String group;

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

    protected AbstractCaller(Method method, RemoteCaller<?> remoteCaller, String protocol, Class<T> annoType) {
        super(method, remoteCaller, protocol, annoType);
    }

    @Override
    public void init() {
        Options ops = options();
        // check
        checkAsyncReturnType(method);
        checkDirectUrl(ops);
        // set
        group(ops.group());
        router(ops.router());
        timeout(ops.timeout());
        retires(ops.retires());
        serviceDiscovery(ops.serviceDiscovery());
        loadBalance(ops.loadBalance());
        faultTolerance(ops.faultTolerance());
        oneWay(returnClass() == Void.TYPE);
        multiplex(ops.multiplex());
        clientConfig(ops.client());
        // subclass init
        doInit();
        // parse core
        ConfigManager configManager = virtue.configManager();
        for (RegistryConfig registryConfig : configManager.registryConfigManager().globalConfigs()) {
            addRegistryConfig(registryConfig);
        }
        String[] registryNames = ops.registries();
        Optional.ofNullable(registryNames)
                .ifPresent(names -> Arrays.stream(names)
                        .map(configManager.registryConfigManager()::get)
                        .filter(Objects::nonNull)
                        .forEach(this::addRegistryConfig));
        ClientConfig clientConfig = checkAndGetClientConfig();
        url = createUrl(clientConfig.toUrl());
    }

    @Override
    public void start() {
        if (!remoteCaller().lazyDiscover() && !isDirectInvoke()) {
            if (CollectionUtil.isEmpty(registryConfigs)) {
                logger.warn("Can't find RegistryConfig(s)");
            } else {
                for (URL registryUrl : registryConfigUrls) {
                    RegistryFactory registryFactory = ExtensionLoader.loadExtension(RegistryFactory.class, registryUrl.protocol());
                    RegistryService registryService = registryFactory.get(registryUrl);
                    registryService.discover(url);
                }
            }
        }
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
                (oldConfig, newConfig) ->
                        Objects.equals(oldConfig.type(), newConfig.type())
                                && oldConfig.host().equals(newConfig.host())
                                && oldConfig.port() == newConfig.port(),
                registryConfig -> {
                    URL registryUrl = registryConfig.toUrl();
                    registryUrl.set(Virtue.LOCAL_VIRTUE, virtue);
                    registryUrl.addParam(Key.LOCAL_VIRTUE, virtue.name());
                    registryConfigUrls.add(registryUrl);
                }, configs);

    }

    @Override
    public Object invoke(Invocation invocation) throws RpcException {
        try {
            String faultToleranceName = invocation.url().getParam(Key.FAULT_TOLERANCE, Constant.DEFAULT_FAULT_TOLERANCE);
            FaultTolerance faultTolerance = ExtensionLoader.loadExtension(FaultTolerance.class, faultToleranceName);
            List<Filter> preFilters = FilterScope.PRE.filterScope(filters);
            invocation.revise(() -> {
                invocation.revise(() -> doRpcCall(invocation));
                return filterChain.filter(invocation, preFilters);
            });
            return faultTolerance.operation(invocation);
        } catch (Exception e) {
            RpcContext.currentContext().set(Key.CALL_EXCEPTION, e);
            if (remoteCaller().fallBacker() != null) {
                return remoteCaller().invokeFallBack(invocation);
            }
            logger.error("Remote call failed " + this, e);
            throw RpcException.unwrap(e);
        } finally {
            RpcContext.clear();
        }
    }

    @Override
    public Type returnType() {
        return returnType;
    }

    @Override
    public Class<?> returnClass() {
        Class<?> returnType = method.getReturnType();
        return async() ? ReflectionUtil.getClassByType(returnType()) : returnType;
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
        url.addParams(clientUrl.params());
        url.addParams(parameterization());
        url.set(Key.LAST_CALL_INDEX_ATTRIBUTE_KEY, new AtomicInteger(-1));
        return url;
    }

    protected Object doRpcCall(Invocation invocation) {
        URL url = invocation.url();
        Caller<?> caller = (Caller<?>) invocation.invoker();
        if (!isDirectInvoke()) {
            // ServiceDiscovery
            String serviceDiscoveryName = url.getParam(Key.SERVICE_DISCOVERY, Constant.DEFAULT_SERVICE_DISCOVERY);
            ServiceDiscovery serviceDiscovery = ExtensionLoader.loadExtension(ServiceDiscovery.class, serviceDiscoveryName);
            URL[] registryConfigUrls = Optional.ofNullable(caller.registryConfigUrls()).stream()
                    .flatMap(Collection::stream)
                    .toArray(URL[]::new);
            List<URL> availableServiceUrls = serviceDiscovery.discover(invocation, registryConfigUrls);
            // Router
            Router router = virtue.get(Router.ATTRIBUTE_KEY);
            if (router == null) {
                String routerName = virtue.configManager().applicationConfig().router();
                routerName = StringUtil.isBlankOrDefault(routerName, Constant.DEFAULT_ROUTER);
                router = ExtensionLoader.loadExtension(Router.class, routerName);
            }
            List<URL> finalServiceUrls = router.route(invocation, availableServiceUrls);
            // LoadBalance
            String loadBalancerName = url.getParam(Key.LOAD_BALANCE, Constant.DEFAULT_LOAD_BALANCE);
            LoadBalancer loadBalancer = ExtensionLoader.loadExtension(LoadBalancer.class, loadBalancerName);
            URL selectedServiceUrl = loadBalancer.choose(invocation, finalServiceUrls);
            url.address(selectedServiceUrl.address()).addParams(selectedServiceUrl.params());
        }
        return call(invocation);
    }

    protected Object call(Invocation invocation) throws RpcException {
        List<Filter> postFilters = FilterScope.POST.filterScope(filters);
        invocation.revise(() -> {
            URL url = invocation.url();
            String requestContextStr = RpcContext.requestContext().toString();
            url.addParam(Key.REQUEST_CONTEXT, requestContextStr);
            url.addParam(Key.ENVELOPE, Key.REQUEST);
            RpcFuture future = protocolInstance.sendRequest(invocation);
            return async() ? future : future.get();
        });
        return filterChain.filter(invocation, postFilters);
    }

    private Options options() {
        if (method.isAnnotationPresent(Options.class)) {
            return method.getAnnotation(Options.class);
        }
        return ReflectionUtil.getDefaultInstance(Options.class);
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

}
