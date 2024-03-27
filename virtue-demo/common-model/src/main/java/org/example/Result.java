package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author WenBo Zhou
 * @Date 2024/3/26 15:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {

    private String code;

    private Object data;

    private String message;

}
