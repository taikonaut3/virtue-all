package io.github.astro.rpc.virtue.envelope;

import io.github.astro.rpc.virtue.header.Header;
import lombok.ToString;

/**
 * @Author WenBo Zhou
 * @Date 2023/12/3 13:29
 */
@ToString
public class VirtueRequest extends AbstractVirtueEnvelope {

    public VirtueRequest() {

    }

    public VirtueRequest(Header header, Object body) {
        super(header, body);
    }

}
