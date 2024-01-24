package io.github.astro.virtue.boot;

import io.github.astro.virtue.common.constant.Key;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.RegistrationLifecycle;

import static io.github.astro.virtue.common.constant.Components.Protocol.HTTP;

public class VirtueRegistrationLifecycle<R extends Registration> implements RegistrationLifecycle<R> {


    @Override
    public void postProcessBeforeStartRegister(R registration) {
        registration.getMetadata().put(Key.PROTOCOL,HTTP);
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
