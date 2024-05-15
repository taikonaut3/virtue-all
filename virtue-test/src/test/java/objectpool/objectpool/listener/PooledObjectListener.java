package objectpool.objectpool.listener;

import io.virtue.event.EventListener;
import objectpool.objectpool.PooledObject;

/**
 * @param <S>
 */
public abstract class PooledObjectListener<S extends PooledObjectEvent<PooledObject<?>>> implements EventListener<S> {
}
