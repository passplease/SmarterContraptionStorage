package net.smartercontraptionstorage.AddStorage;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public interface SerializableHandler<T> {
    default boolean canDeserialize(){
        return true;
    }

    String getName();

    @NotNull
    T deserialize(CompoundTag nbt) throws IllegalAccessException;
}
