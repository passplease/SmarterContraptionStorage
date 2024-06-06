package net.morestorageforcreate.FunctionInterface;
@FunctionalInterface
public interface DoubleFunction<T,U,R> {
    R function(T t,U u);
}
