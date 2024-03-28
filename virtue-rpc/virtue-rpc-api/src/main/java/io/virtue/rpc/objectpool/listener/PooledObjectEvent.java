package io.virtue.rpc.objectpool.listener;

import io.virtue.event.AbstractEvent;
import io.virtue.rpc.objectpool.PooledObject;

/**
 * PooledObjectEvent.
 *
 * @param <S>
 */
public abstract class PooledObjectEvent<S extends PooledObject<?>> extends AbstractEvent<S> {

}
