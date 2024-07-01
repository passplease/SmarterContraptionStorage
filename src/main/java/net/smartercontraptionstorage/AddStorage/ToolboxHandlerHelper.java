package net.smartercontraptionstorage.AddStorage;

import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;

public class ToolboxHandlerHelper extends StorageHandlerHelper{
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof ToolboxBlockEntity;
    }

    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {
        assert canCreateHandler(entity) && handler instanceof ToolboxInventory;
        ((ToolboxBlockEntity)entity).readInventory(handler.serializeNBT());
    }

    @Override
    public ItemStackHandler createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        ToolboxInventory inventory = new ToolboxInventory((ToolboxBlockEntity) entity);
        inventory.deserializeNBT(entity.serializeNBT().getCompound("Inventory"));
        return inventory;
    }
    @Override
    public boolean allowControl(Item comparedItem) {
        return false;
    }

    @Override
    public boolean allowControl(Block block) {
        return block instanceof ToolboxBlock;
    }

}