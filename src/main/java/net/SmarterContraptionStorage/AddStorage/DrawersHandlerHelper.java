package net.SmarterContraptionStorage.AddStorage;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawersComp;
import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class DrawersHandlerHelper extends StorageHandlerHelper {
    @Override
    protected boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof BlockEntityDrawers && !(entity instanceof BlockEntityDrawersComp);
    }
    @Override
    public void addStorageToWorld(BlockEntity entity,ItemStackHandler handler) {
        IDrawerGroup group = ((BlockEntityDrawers) entity).getGroup();
        for(int i = handler.getSlots() - 1;i >= 0;i--){
            group.getDrawer(i).setStoredItemCount(handler.getStackInSlot(i).getCount());
        }
    }
    @Override
    public ItemStackHandler createHandler(BlockEntity entity) {
        IDrawerGroup group = ((BlockEntityDrawers) entity).getGroup();
        return new NormalDrawerHandler(group);
    }
    @Override
    public boolean allowControl(Item comparedItem) {
        Block block = Block.byItem(comparedItem);
        return allowControl(block);
    }
    public boolean allowControl(Block block){
        return block instanceof BlockDrawers && !(block instanceof BlockCompDrawers);
    }
    protected static class NormalDrawerHandler extends HandlerHelper{
        public NormalDrawerHandler(@NotNull IDrawerGroup group) {
            super(group.getDrawerCount());
            for(int i = slotLimits.length - 1;i >= 0;i--){
                if(group.getDrawer(i).getAcceptingRemainingCapacity() == Integer.MAX_VALUE)
                    slotLimits[i] = Integer.MAX_VALUE;
                else slotLimits[i] = group.getDrawer(i).getMaxCapacity();
                items[i] = group.getDrawer(i).getStoredItemPrototype();
                items[i].setCount(group.getDrawer(i).getStoredItemCount());
            }
        }
        protected boolean canInsert(int slot,ItemStack stack){
            if(items[slot] == ItemStack.EMPTY)
                return true;
            return items[slot].sameItem(stack);
        }
        @Override
        public int getStackLimit(int slot, @NotNull ItemStack stack) {
            if(canInsert(slot,stack))
                return slotLimits[slot];
            else return 0;
        }
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if(canInsert(slot,stack)){
                if(items[slot].isEmpty()) {
                    items[slot] = stack;
                    return ItemStack.EMPTY;
                }
                if(simulate){
                    if(stack.getCount() <= slotLimits[slot]) {
                        items[slot].setCount(stack.getCount());
                        return ItemStack.EMPTY;
                    }
                    else {
                        items[slot].setCount(slotLimits[slot]);
                        stack.setCount(stack.getCount() - slotLimits[slot]);
                        return stack;
                    }
                }
                if(items[slot].getCount() + stack.getCount() > slotLimits[slot]) {
                    stack.grow(slotLimits[slot] - items[slot].getCount());
                    items[slot].setCount(slotLimits[slot]);
                    return stack;
                }else {
                    items[slot].grow(stack.getCount());
                    return ItemStack.EMPTY;
                }
            }else return stack;
        }
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(amount == 0)
                return ItemStack.EMPTY;
            validateSlotIndex(slot);
            ItemStack existing = getStackInSlot(slot);
            if(existing.isEmpty())
                return ItemStack.EMPTY;
            final int toExtract = Math.min(existing.getCount(),amount);
            if(existing.getCount() <= toExtract)
                if(!simulate){
                    items[slot] = ItemStack.EMPTY;
                    onContentsChanged(slot);
                    return existing;
                }else return existing.copy();
            else {
                if(!simulate) {
                    existing.grow(-toExtract);
                    onContentsChanged(slot);
                }
                return ItemHandlerHelper.copyStackWithSize(existing,amount);
            }
        }
    }
}