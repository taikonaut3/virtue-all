package io.virtue.transport;

import io.virtue.common.constant.Key;
import io.virtue.common.url.URL;

import java.io.Serializable;


public interface Envelope extends Serializable {

    long serialVersionUID = 1L;

    URL url();

    default Long id() {
        return url().getLongParam(Key.UNIQUE_ID);
    }

}
