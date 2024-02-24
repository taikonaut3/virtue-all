package io.github.taikonaut3.virtue.rpc.objectfactory.listener;

import io.github.taikonaut3.virtue.event.EventListener;
import io.github.taikonaut3.virtue.rpc.objectfactory.PooledObject;

/**
 * @author Chang Liu
 */
public abstract class PooledObjectListener<S extends PooledObjectEvent<PooledObject<?>>> implements EventListener<S> {
}
