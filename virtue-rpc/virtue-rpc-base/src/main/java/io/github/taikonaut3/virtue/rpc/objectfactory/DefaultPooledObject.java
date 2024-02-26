package io.github.taikonaut3.virtue.rpc.objectfactory;
/**
 * @author Chang Liu
 */
public class DefaultPooledObject<T> extends AbstractPooledObject<T>{


    public DefaultPooledObject(final T object){
        super(object);
    }
}
