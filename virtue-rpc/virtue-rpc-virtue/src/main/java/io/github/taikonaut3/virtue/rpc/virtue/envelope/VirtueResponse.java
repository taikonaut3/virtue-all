package io.github.taikonaut3.virtue.rpc.virtue.envelope;

import io.github.taikonaut3.virtue.common.url.URL;
import lombok.ToString;

@ToString
public class VirtueResponse extends VirtueEnvelope {

    public VirtueResponse() {

    }

    public VirtueResponse(URL url, Object payload) {
        super(url, payload);
    }

}
