package net.smartercontraptionstorage.Interface;

import javax.annotation.Nullable;

@Deprecated
public interface Gettable {
    @Nullable
    Object get(String name);
}
