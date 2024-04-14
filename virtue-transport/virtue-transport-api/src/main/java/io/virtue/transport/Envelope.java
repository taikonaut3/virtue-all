package io.virtue.transport;

import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;

import java.io.Serializable;

/**
 * The exchange message's envelope .
 */
public interface Envelope extends Serializable {

    long serialVersionUID = 1L;

    /**
     * Get url.
     *
     * @return
     */
    URL url();

    /**
     * Get unique id.
     *
     * @return
     */
    default long id() {
        return url().getLongParam(Key.UNIQUE_ID);
    }

}
