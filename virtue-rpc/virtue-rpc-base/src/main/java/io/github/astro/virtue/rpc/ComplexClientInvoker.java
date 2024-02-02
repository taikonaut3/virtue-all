package io.github.astro.virtue.rpc;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.exception.RpcException;
import io.github.astro.virtue.common.exception.SourceException;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.common.util.StringUtil;
import io.github.astro.virtue.config.ClientCaller;
import io.github.astro.virtue.config.Invocation;
import io.github.astro.virtue.config.Invoker;
import io.github.astro.virtue.config.manager.Virtue;
import io.github.astro.virtue.governance.directory.Directory;
import io.github.astro.virtue.governance.faulttolerance.FaultTolerance;
import io.github.astro.virtue.governance.loadbalance.LoadBalance;
import io.github.astro.virtue.governance.router.Router;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ComplexClientInvoker implements Invoker<Object> {

    private final ClientCaller<?> caller;

    private Directory directory;

    private Router router;

    private LoadBalance loadBalance;

    private FaultTolerance faultTolerance;

    public ComplexClientInvoker(ClientCaller<?> caller) {
        this.caller = caller;
        initCallComponent();

    }

    public void initCallComponent() {
        this.faultTolerance = ExtensionLoader.loadService(FaultTolerance.class, caller.faultTolerance());
        this.loadBalance = ExtensionLoader.loadService(LoadBalance.class, caller.loadBalance());
        this.router = ExtensionLoader.loadService(Router.class, caller.router());
        this.directory = ExtensionLoader.loadService(Directory.class, caller.directory());
    }

    @Override
    public Object invoke(Invocation invocation) throws RpcException {
        try {
            URL url = selectURL(invocation);
            url.addParameters(caller.parameterization());
            invocation.url(url);
            return faultTolerance.operation(invocation.turnInvoke(caller::call));
        } catch (RpcException e) {
            throw new RpcException(e);
        }
    }

    public URL selectURL(Invocation invocation) {
        if (!StringUtil.isBlank(caller.directUrl())) {
            return caller.url();
        }
        List<URL> urls = discoveryUrls(invocation);
        urls = router.route(urls, invocation);
        return loadBalance.select(invocation, urls);
    }

    public List<URL> discoveryUrls(Invocation invocation) {
        URL[] urls = caller.registryConfigs().stream()
                .map(config -> {
                    URL url = config.toUrl();
                    url.attribute(Virtue.ATTRIBUTE_KEY).set(caller.container().virtue());
                    url.addParameter(Key.VIRTUE, caller.container().virtue().name());
                    return url;
                }).
                toArray(URL[]::new);
        List<URL> result = directory.list(invocation, urls);
        if (result.isEmpty()) {
            if (caller.lazyDiscover()) {
                throw new SourceException("Not found available service!,Path:" + invocation.url().path());
            }
        }
        return result;
    }
}
