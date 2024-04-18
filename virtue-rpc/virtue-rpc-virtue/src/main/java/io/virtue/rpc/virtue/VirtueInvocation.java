package io.virtue.rpc.virtue;

import io.virtue.common.url.URL;
import io.virtue.core.Callee;
import io.virtue.core.Caller;
import io.virtue.core.support.TransferableInvocation;

/**
 * Virtue Invocation.
 */
public class VirtueInvocation extends TransferableInvocation {

    public VirtueInvocation() {

    }

    public VirtueInvocation(Caller<?> caller, Object[] args) {
        super(caller, args);
    }

    public VirtueInvocation(URL url, Callee<?> callee, Object[] args) {
        super(url, callee, args);
    }
}
