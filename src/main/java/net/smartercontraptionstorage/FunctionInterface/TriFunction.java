package net.smartercontraptionstorage.FunctionInterface;
@FunctionalInterface
public interface TriFunction<T,R,U,W> {
    W function(T t,R r,U u);
    default W function(T t,R r){
        return function(t,r,null);
    }
}
