package io.virtue.registry.support;

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

    private final Virtue virtue;

    public RegisterServiceEvent(Virtue virtue, RegisterTask runnable) {
        super(runnable);
        this.virtue = virtue;
    }
}
