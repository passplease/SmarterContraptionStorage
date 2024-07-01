package net.smartercontraptionstorage.FunctionInterface;
@FunctionalInterface
public interface FiveFunction<T,U,R,W,A,V> {
    V function(T t,U u,R r,W w,A a);
    default V function(T t,U u,R r,W w){
        return function(t,u,r,w,null);
    }
}
