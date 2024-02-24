package io.github.taikonaut3.virtue.rpc.objectfactory.listener;

import io.github.taikonaut3.virtue.event.AbstractEvent;
import io.github.taikonaut3.virtue.rpc.objectfactory.PooledObject;

/**
 * @author Chang Liu
 */
public abstract class PooledObjectEvent<S extends PooledObject<?>> extends AbstractEvent<S>{
    private S source;
    @Override
    public S source() {
        return source;
    }

    @Override
    public void source(S source) {
        this.source = source;
    }
}
