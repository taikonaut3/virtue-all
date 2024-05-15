package objectpool.objectpool;

import io.virtue.core.Virtue;
import objectpool.objectpool.listener.PooledObjectInvalidEvent;
import objectpool.objectpool.listener.PooledObjectInvalidListener;

/**
 * DefaultPooledObject.
 *
 * @param <T>
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
        virtue.eventDispatcher().dispatch(state.getEvent(this));
    }

    /**
     * Set virtue.
     *
     * @param virtue
     */
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
