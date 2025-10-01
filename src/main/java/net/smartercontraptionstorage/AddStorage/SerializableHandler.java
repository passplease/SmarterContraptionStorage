package net.smartercontraptionstorage.AddStorage;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface SerializableHandler<T> {
    default boolean canDeserialize(){
        return true;
    }

    String getName();

    @NotNull
    T deserialize(CompoundTag nbt, HolderLookup.Provider provider, boolean client) throws IllegalAccessException;

    boolean canCreateHandler(BlockEntity entity);

    @NotNull T createHandler(BlockEntity entity);

    void registerBlock(Consumer<Block> register);
}
