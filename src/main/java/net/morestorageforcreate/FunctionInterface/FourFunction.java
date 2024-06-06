package net.morestorageforcreate.FunctionInterface;
@FunctionalInterface
public interface FourFunction<T,U,R,W,A> {
    A function(T t,U u,R r,W w);
}
