package org.example;

import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.QueryParam;
import lombok.Data;

/**
 * @Author WenBo Zhou
 * @Date 2024/4/25 9:43
 */
@Data
public class MyBean {

    @HeaderParam("mybean-name")
    private String name = "mybeanName";

    @QueryParam("mybean-age")
    private int age = 23;

    @HeaderParam("mybean-sex")
    private String sex = "man";
}
