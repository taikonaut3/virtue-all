package org.example.model1;

import lombok.Data;

/**
 * @Author WenBo Zhou
 * @Date 2024/4/1 13:43
 */
@Data
public class TestGeneric<T> {

    private T data;

}
