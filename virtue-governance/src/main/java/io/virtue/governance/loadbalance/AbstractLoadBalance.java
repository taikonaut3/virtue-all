package io.virtue.governance.loadbalance;

import io.virtue.common.exception.SourceException;
import io.virtue.common.url.URL;
import io.virtue.config.Invocation;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public URL select(Invocation invocation, List<URL> urls) {
        if (urls.isEmpty()) {
            throw new SourceException("Not available Service Urls");
        }
        if (urls.size() == 1) {
            return urls.get(0);
        }
        return doSelect(invocation, urls);
    }

    protected abstract URL doSelect(Invocation invocation, List<URL> urls);

}
