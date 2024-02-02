package io.github.astro.virtue.rpc;

import io.github.astro.virtue.common.exception.RpcException;
import io.github.astro.virtue.common.spi.ExtensionLoader;
import io.github.astro.virtue.common.url.URL;
import io.github.astro.virtue.config.CallArgs;
import io.github.astro.virtue.config.Invocation;
import io.github.astro.virtue.config.Invoker;
import io.github.astro.virtue.config.ServerCaller;
import io.github.astro.virtue.config.filter.Filter;
import io.github.astro.virtue.config.filter.FilterChain;
import io.github.astro.virtue.config.filter.FilterScope;
import io.github.astro.virtue.rpc.protocol.Protocol;
import lombok.Data;

import java.util.List;

@Data
public class ComplexServerInvoker implements Invoker<Object> {

    private ServerCaller<?> caller;

    private Protocol<?,?> protocol;

    public ComplexServerInvoker(ServerCaller<?> caller) {
        this.caller = caller;
        this.protocol = ExtensionLoader.loadService(Protocol.class, caller.protocol());
    }

    @Override
    public Object invoke(Invocation invocation) throws RpcException {
        URL url = invocation.url();
        CallArgs callArgs = invocation.callArgs();
        FilterChain filterChain = caller.filterChain();
        List<Filter> filters = FilterScope.PRE.filterScope(caller.filters());
        invocation.turnInvoke(inv -> caller.call(callArgs));
        Object result = filterChain.filter(invocation, filters);
        url.addParameters(caller.parameterization());
        return protocol.createResponse(url, result);
    }
}
