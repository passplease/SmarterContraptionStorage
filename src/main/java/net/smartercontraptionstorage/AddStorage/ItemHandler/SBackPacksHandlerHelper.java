package net.smartercontraptionstorage.AddStorage.ItemHandler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlockEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import org.jetbrains.annotations.NotNull;

public class SBackPacksHandlerHelper extends StorageHandlerHelper{
    public static final String NAME = "SBackPacksHandlerHelper";
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof BackpackBlockEntity;
    }

    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {
        assert canCreateHandler(entity);
        ((InventoryHandler)handler).copyStacksTo(((BackpackBlockEntity)entity).getStorageWrapper().getInventoryHandler());
    }

    @Override
    public @NotNull ItemStackHandler createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        return ((BackpackBlockEntity)entity).getStorageWrapper().getInventoryHandler();
    }

    @Override
    public boolean allowControl(Item comparedItem) {
        return comparedItem instanceof BackpackItem;
    }

    @Override
    public boolean allowControl(Block block) {
        return block instanceof BackpackBlock;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean canDeserialize() {
        return false;
    }

    @Override
    public @NotNull ItemStackHandler deserialize(CompoundTag nbt) throws IllegalAccessException {
        throw new IllegalAccessException();
    }
}
