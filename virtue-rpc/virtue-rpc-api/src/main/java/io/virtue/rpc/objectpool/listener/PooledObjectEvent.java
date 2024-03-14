package io.virtue.rpc.objectpool.listener;

import io.virtue.event.AbstractEvent;
import io.virtue.rpc.objectpool.PooledObject;

/**
 * @author Chang Liu
 */
public abstract class PooledObjectEvent<S extends PooledObject<?>> extends AbstractEvent<S>{

}
