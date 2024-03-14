package io.virtue.boot;

import io.virtue.common.constant.Key;
import io.virtue.config.config.ApplicationConfig;
import io.virtue.config.manager.Virtue;
import io.virtue.common.constant.Components;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.RegistrationLifecycle;

public class VirtueRegistrationLifecycle<R extends Registration> implements RegistrationLifecycle<R> {

    private final Virtue virtue;

    public VirtueRegistrationLifecycle(Virtue virtue) {
        this.virtue = virtue;
    }

    @Override
    public void postProcessBeforeStartRegister(R registration) {
        ApplicationConfig config = virtue.configManager().applicationConfig();
        registration.getMetadata().put(Key.PROTOCOL, Components.Protocol.HTTP);
        registration.getMetadata().put(Key.WEIGHT, String.valueOf(config.weight()));
    }

    @Override
    public void postProcessAfterStartRegister(R registration) {

    }

    @Override
    public void postProcessBeforeStopRegister(R registration) {

    }

    @Override
    public void postProcessAfterStopRegister(R registration) {

    }

}
