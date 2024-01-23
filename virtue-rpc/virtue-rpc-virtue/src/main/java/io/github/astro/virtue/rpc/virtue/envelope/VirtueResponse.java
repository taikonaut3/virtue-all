package io.github.astro.virtue.rpc.virtue.envelope;

import io.github.astro.virtue.rpc.virtue.header.Header;
import lombok.ToString;

/**
 * @Author WenBo Zhou
 * @Date 2023/12/3 13:29
 */
@ToString
public class VirtueResponse extends AbstractVirtueEnvelope {

    public VirtueResponse() {

    }

    public VirtueResponse(Header header, Object payload) {
        super(header, payload);
    }

}
