package net.smartercontraptionstorage.Interface;
@FunctionalInterface
public interface FourFunction<T,U,R,W,A> {
    A function(T t,U u,R r,W w);
    default A function(T t,U u,R r){
        return function(t,u,r,null);
    }
}
