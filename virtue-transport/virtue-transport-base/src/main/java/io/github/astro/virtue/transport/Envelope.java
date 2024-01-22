package io.github.astro.virtue.transport;

import io.github.astro.virtue.common.constant.Key;
import io.github.astro.virtue.common.url.URL;

import java.io.Serializable;

/**
 * @Author WenBo Zhou
 * @Date 2023/12/3 15:34
 */
public interface Envelope extends Serializable {

    long serialVersionUID = 1L;

    URL url();

    default Long id() {
        return url().getLongParameter(Key.UNIQUE_ID);
    }

}
