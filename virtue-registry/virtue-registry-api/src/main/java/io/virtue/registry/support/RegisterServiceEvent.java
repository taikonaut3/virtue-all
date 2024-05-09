package io.virtue.registry.support;

import io.virtue.common.url.URL;
import io.virtue.core.Virtue;
import io.virtue.event.AbstractEvent;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * RegisterService Event.
 */
@Getter
@Accessors(fluent = true)
public class RegisterServiceEvent extends AbstractEvent<RegisterTask> {

    private final URL url;

    private final Virtue virtue;

    public RegisterServiceEvent(URL url, RegisterTask runnable) {
        super(runnable);
        this.url = url;
        this.virtue = Virtue.ofLocal(url);
    }
}
