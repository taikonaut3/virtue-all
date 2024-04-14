package org.example.model2;

import java.io.Serializable;

public class GrandchildObject implements Serializable {

    private String someValue;

    // getters 和 setters

    public String getSomeValue() {
        return someValue;
    }

    public void setSomeValue(String someValue) {
        this.someValue = someValue;
    }

}
