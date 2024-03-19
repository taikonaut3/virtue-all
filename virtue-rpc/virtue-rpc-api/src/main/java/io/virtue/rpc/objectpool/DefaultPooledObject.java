package io.virtue.rpc.objectpool;

import io.virtue.core.manager.Virtue;
import io.virtue.rpc.objectpool.listener.PooledObjectInvalidEvent;
import io.virtue.rpc.objectpool.listener.PooledObjectInvalidListener;

/**
 * @author Chang Liu
 */
public class DefaultPooledObject<T> extends AbstractPooledObject<T> {

    private Virtue virtue;

    private volatile boolean noHadRegisterEvent = false;

    public DefaultPooledObject(final T object) {
        super(object);
    }

    @Override
    public void state(PooledObjectState state) {
        this.state = state;
        virtue.eventDispatcher().dispatchEvent(state.getEvent(this));
    }

    public void virtue(Virtue virtue) {
        this.virtue = virtue;
        synchronized (object) {
            if (noHadRegisterEvent) {
                virtue.eventDispatcher().addListener(PooledObjectInvalidEvent.class, new PooledObjectInvalidListener<>());
                noHadRegisterEvent = true;
            }
        }
    }
}
