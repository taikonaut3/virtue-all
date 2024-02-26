package io.github.taikonaut3.virtue.rpc.objectpool.listener;

import io.github.taikonaut3.virtue.event.EventListener;
import io.github.taikonaut3.virtue.rpc.objectpool.PooledObject;

/**
 * @author Chang Liu
 */
public abstract class PooledObjectListener<S extends PooledObjectEvent<PooledObject<?>>> implements EventListener<S> {
}
