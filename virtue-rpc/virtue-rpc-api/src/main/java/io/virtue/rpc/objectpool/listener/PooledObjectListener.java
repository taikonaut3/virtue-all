package io.virtue.rpc.objectpool.listener;

import io.virtue.event.EventListener;
import io.virtue.rpc.objectpool.PooledObject;

/**
 * @author Chang Liu
 */
public abstract class PooledObjectListener<S extends PooledObjectEvent<PooledObject<?>>> implements EventListener<S> {
}
