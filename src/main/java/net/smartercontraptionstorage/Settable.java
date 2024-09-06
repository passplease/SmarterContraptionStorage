package net.smartercontraptionstorage;

public interface Settable {
    void set(Object object);
    default void set(String parameterName, Object object){
        set(object);
    }
}
