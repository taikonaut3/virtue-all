package objectpool.objectpool.listener;

import io.virtue.event.AbstractEvent;
import objectpool.objectpool.PooledObject;

/**
 * PooledObjectEvent.
 *
 * @param <S>
 */
public abstract class PooledObjectEvent<S extends PooledObject<?>> extends AbstractEvent<S> {

}
