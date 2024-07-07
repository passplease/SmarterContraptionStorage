package net.smartercontraptionstorage.AddStorage;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlockEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;

public class SBackPacksHandlerHelper extends StorageHandlerHelper{
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof BackpackBlockEntity;
    }

    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {
        ((InventoryHandler)handler).copyStacksTo(((BackpackBlockEntity)entity).getStorageWrapper().getInventoryHandler());
    }

    @Override
    public ItemStackHandler createHandler(BlockEntity entity) {
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
}