package io.github.astro.virtue.common.extension;

public class Attribute<T> {

    private T attribute;

    public T get() {
        return attribute;
    }

    public void set(T attribute) {
        this.attribute = attribute;
    }
}
