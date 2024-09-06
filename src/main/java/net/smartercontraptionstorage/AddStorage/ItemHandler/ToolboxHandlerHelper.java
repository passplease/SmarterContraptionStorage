package net.smartercontraptionstorage.AddStorage.ItemHandler;

import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandler;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.smartercontraptionstorage.AddStorage.NeedDealWith;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.NotNull;

public class ToolboxHandlerHelper extends StorageHandlerHelper{
    public static final String NAME = "ToolboxHandlerHelper";
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof ToolboxBlockEntity;
    }

    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {
        assert canCreateHandler(entity) && handler instanceof ToolboxInventory;
        ToolboxHandler.onUnload((ToolboxBlockEntity) entity);
        ((ToolboxBlockEntity)entity).readInventory(handler.serializeNBT());
    }

    @Override
    public @NotNull ItemStackHandler createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        ToolboxHandler.onLoad((ToolboxBlockEntity) entity);
        ToolboxHelper inventory = new ToolboxHelper((ToolboxBlockEntity) entity);
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

    public static class ToolboxHelper extends ToolboxInventory implements NeedDealWith {
        public ToolboxHelper(ToolboxBlockEntity be) {
            super(be);
        }

        @Override
        public void doSomething(BlockEntity entity) {
            StorageHandlerHelper.BlockEntityList.add(entity);
            Utils.addInventory((ToolboxBlockEntity) entity);
        }

        @Override
        public void finallyDo() {}
    }
}