package net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper;
import net.smartercontraptionstorage.AddStorage.NeedDealWith;
import org.jetbrains.annotations.NotNull;

public abstract class InitializeHelper extends StorageHandlerHelper implements NeedDealWith {
    @Override
    public final boolean canCreateHandler(BlockEntity entity) {
        return canDoSomething(entity);
    }

    public abstract boolean canDoSomething(BlockEntity entity);

    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {
        throw new IllegalCallerException("Can not invoke this method !");
    }

    @Override
    public final @NotNull ItemStackHandler createHandler(BlockEntity entity) {
        doSomething(entity);
        return nullHandler;
    }

    @Override
    public final boolean allowControl(Item comparedItem) {
        return false;
    }

    @Override
    public final boolean allowControl(Block block) {
        return false;
    }

    @Override
    public final void finallyDo() {}

    protected final void normallyDo(BlockEntity entity) {
        assert canCreateHandler(entity);
        StorageHandlerHelper.BlockEntityList.add(entity);
    }

    @Override
    public CompoundTag serializeNBT() {
        return new CompoundTag();
    }

    @Override
    public final String getName() {
        return "InitializeHelper";
    }

    @Override
    public final ItemStackHandler deserialize(CompoundTag nbt) {
        return nullHandler;
    }
}