package net.smartercontraptionstorage.AddStorage.ItemHandler;

import com.simibubi.create.foundation.utility.NBTHelper;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
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

    public static class TrashHandler extends HandlerHelper implements NeedDealWith {
        public final boolean whiteOrBlack;
        public List<ItemStack> toolboxItem;
        // false : black
        // true : white
        public TrashHandler(boolean whiteOrBlack, ArrayList<ItemStack> itemFilter) {
            super(itemFilter.size());
            this.whiteOrBlack = whiteOrBlack;
            for (int i = items.length - 1; i >= 0; i--) {
                items[i] = itemFilter.get(i);
                slotLimits[i] = Integer.MAX_VALUE;
            }
        }
        protected boolean canDelete(ItemStack stack){
            for (ItemStack itemStack : toolboxItem) {
                if(ItemStack.isSameItem(stack,itemStack))
                    return false;
            }
            for (ItemStack item : items){
                if(ItemStack.isSameItem(item,stack))
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
        public void doSomething(BlockEntity entity) {}
        @Override
        public void finallyDo() {
            this.toolboxItem = NBTHelper.readItemList(Utils.getInventory().getList("Compartments", Tag.TAG_COMPOUND));
        }
    }
}