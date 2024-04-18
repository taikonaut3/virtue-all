package io.virtue.rpc.virtue.envelope;

import io.virtue.common.url.URL;
import lombok.ToString;

/**
 * Virtue Response.
 */
@ToString
public class VirtueResponse extends VirtueEnvelope {

    private boolean hasException;

    public VirtueResponse() {

    }

    public VirtueResponse(URL url, Object payload, boolean hasException) {
        super(url, payload);
        this.hasException = hasException;
    }

    public boolean hasException() {
        return hasException;
    }

    public void hasException(boolean hasException) {
        this.hasException = hasException;
    }

}
