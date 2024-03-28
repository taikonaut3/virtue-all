package io.virtue.rpc.objectpool.listener;

import io.virtue.event.EventListener;
import io.virtue.rpc.objectpool.PooledObject;

/**
 *
 * @param <S>
 */
public abstract class PooledObjectListener<S extends PooledObjectEvent<PooledObject<?>>> implements EventListener<S> {
}
