package io.virtue.rpc.virtue.envelope;

import io.virtue.common.url.URL;
import lombok.ToString;

@ToString
public class VirtueRequest extends VirtueEnvelope {

    public VirtueRequest() {

    }

    public VirtueRequest(URL url, Object body) {
        super(url, body);
    }

}
