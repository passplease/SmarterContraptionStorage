package net.smartercontraptionstorage.AddStorage.ItemHandler;

import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.smartercontraptionstorage.AddStorage.NeedDealWith;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TrashHandlerHelper extends StorageHandlerHelper{
    public static final String NAME = "TrashHandlerHelper";
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof TrashCanBlockEntity && ((TrashCanBlockEntity)entity).items;
    }
    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {
        assert canCreateHandler(entity) && handler instanceof TrashHandler;
        if(entity instanceof TrashCanBlockEntity Entity && handler instanceof TrashHandler Handler) {
            Entity.itemFilterWhitelist = Handler.whiteOrBlack;
            for (int i = Entity.itemFilter.size() - 1; i >= 0; i--)
                Entity.itemFilter.set(i, Handler.items[i]);
            entity.setChanged();
        }
    }
    @Override
    public @NotNull ItemStackHandler createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        TrashCanBlockEntity Entity = (TrashCanBlockEntity)entity;
        return new TrashHandler(Entity.itemFilterWhitelist,Entity.itemFilter);
    }
    @Override
    public boolean allowControl(Item comparedItem) {
        String name = comparedItem.getDescriptionId();
        return name.startsWith("trashcans.block.item") || name.startsWith("trashcans.block.ultimate");
    }
    @Override
    public boolean allowControl(Block block) {
        return false;
    }
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public @NotNull ItemStackHandler deserialize(CompoundTag nbt) {
        return new TrashHandler(nbt);
    }

    public static class TrashHandler extends HandlerHelper implements NeedDealWith {
        public final boolean whiteOrBlack;
        public List<ItemStack> toolboxItem;
        // false : black
        // true : white
        public TrashHandler(boolean whiteOrBlack, List<ItemStack> itemFilter) {
            super(itemFilter.size());
            this.whiteOrBlack = whiteOrBlack;
            for (int i = items.length - 1; i >= 0; i--) {
                items[i] = itemFilter.get(i);
                slotLimits[i] = Integer.MAX_VALUE;
            }
        }
        public TrashHandler(CompoundTag nbt){
            super(nbt);
            whiteOrBlack = nbt.getBoolean("whiteOrBlack");
            toolboxItem = NBTHelper.readItemList(nbt.getList("toolboxItem", Tag.TAG_COMPOUND));
        }
        protected boolean canDelete(ItemStack stack){
            for (ItemStack item : toolboxItem) {
                if(Utils.isSameItem(item,stack))
                    return false;
            }
            for (ItemStack item : items){
                if(Utils.isSameItem(item,stack))
                    return whiteOrBlack;
            }
            return !whiteOrBlack;
        }
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return canDelete(stack) ? ItemStack.EMPTY : stack;
        }
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
        @Override
        public String getName() {
            return NAME;
        }
        @Override
        public void doSomething(BlockEntity entity) {}
        @Override
        public void finallyDo() {
            ArrayList<ItemStack> toolboxItem = new ArrayList<>();
            for(BlockEntity entity : BlockEntityList)
                if(entity instanceof ToolboxBlockEntity){
                    toolboxItem.addAll(NBTHelper.readItemList(entity.serializeNBT().getCompound("Inventory").getList("Compartments", Tag.TAG_COMPOUND)));
                }
            this.toolboxItem = toolboxItem.stream().filter((item)->!item.isEmpty()).toList();
        }
        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            tag.putBoolean("whiteOrBlack",whiteOrBlack);
            tag.put("toolboxItem",NBTHelper.writeItemList(toolboxItem));
            return tag;
        }
    }
}