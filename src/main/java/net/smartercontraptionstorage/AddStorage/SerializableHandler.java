package net.smartercontraptionstorage.AddStorage;

import net.minecraft.client.Minecraft;
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

    @Deprecated
    @NotNull
    default T deserialize(CompoundTag nbt) throws NullPointerException,IllegalAccessException {
        if(Minecraft.getInstance().level == null)
            throw new NullPointerException();
        return deserialize(nbt, Minecraft.getInstance().level.registryAccess(),Minecraft.getInstance().level.isClientSide());
    }

    T deserialize(CompoundTag nbt, HolderLookup.Provider provider, boolean client) throws IllegalAccessException;

    void registerBlock(Consumer<Block> register);

    boolean canCreateHandler(BlockEntity entity);

    @NotNull T createHandler(BlockEntity entity);

    void addStorageToWorld(BlockEntity entity, T handler);
}
