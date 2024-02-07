package io.github.astro.virtue.rpc.virtue.envelope;

import io.github.astro.virtue.rpc.virtue.header.Header;
import lombok.ToString;

@ToString
public class VirtueRequest extends AbstractVirtueEnvelope {

    public VirtueRequest() {

    }

    public VirtueRequest(Header header, Object body) {
        super(header, body);
    }

}
