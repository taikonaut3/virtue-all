package io.github.taikonaut3.virtue.rpc.objectpool;

import io.github.taikonaut3.virtue.config.manager.Virtue;
import io.github.taikonaut3.virtue.rpc.objectpool.listener.PooledObjectInvalidEvent;
import io.github.taikonaut3.virtue.rpc.objectpool.listener.PooledObjectInvalidListener;

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
