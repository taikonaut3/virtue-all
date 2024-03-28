package io.virtue.rpc.virtue.envelope;

import io.virtue.common.url.URL;
import lombok.ToString;

/**
 * Virtue Response.
 */
@ToString
public class VirtueResponse extends VirtueEnvelope {

    public VirtueResponse() {

    }

    public VirtueResponse(URL url, Object payload) {
        super(url, payload);
    }

}
