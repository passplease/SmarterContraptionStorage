package net.smartercontraptionstorage.AddStorage;

import com.buuz135.functionalstorage.block.DrawerBlock;
import com.buuz135.functionalstorage.block.tile.DrawerTile;
import com.buuz135.functionalstorage.inventory.BigInventoryHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import static com.buuz135.functionalstorage.inventory.BigInventoryHandler.*;

public class FunctionalDrawersHandlerHelper extends StorageHandlerHelper{
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof DrawerTile;
    }

    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {
        ((DrawerTile)entity).handler.deserializeNBT(handler.serializeNBT());
    }

    @Override
    public ItemStackHandler createHandler(BlockEntity entity) {
        return new FDrawersHandler(((DrawerTile)entity).getHandler());
    }

    @Override
    public boolean allowControl(Item comparedItem) {
        return comparedItem instanceof DrawerBlock.DrawerItem;
    }

    @Override
    public boolean allowControl(Block block) {
        return block instanceof DrawerBlock;
    }
    public static class FDrawersHandler extends HandlerHelper{
        public final int[] count;
        public final boolean isCreative;
        public FDrawersHandler(BigInventoryHandler handler){
            super(handler.getSlots());
            count = new int[handler.getSlots()];
            this.isCreative = handler.isCreative();
            for (int i = slotLimits.length - 1; i >= 0; i--) {
                slotLimits[i] = handler.getSlotLimit(i);
                items[i] = handler.getStackInSlot(i);
                count[i] = items[i].getCount();
                items[i].setCount(1);
            }
        }
        public boolean canInsert(int slot,@NotNull ItemStack stack){
            return !stack.isEmpty() && (ItemStack.isSameItem(stack,items[slot]) || items[slot].is(Items.AIR));
        }
        public void setCountInSlot(int slot, @NotNull ItemStack stack, int count){
            if(items[slot].is(Items.AIR))
                items[slot] = stack.copy();
            this.count[slot] = count;
        }
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if(canInsert(slot,stack)){
                if(isCreative)
                    return ItemStack.EMPTY;
                int sumCount = count[slot] + stack.getCount();
                if(sumCount >= slotLimits[slot]){
                    if(!simulate)
                        setCountInSlot(slot,stack,slotLimits[slot]);
                    stack.setCount(slotLimits[slot] - sumCount);
                    return stack;
                }else {
                    if(!simulate)
                        setCountInSlot(slot,stack,sumCount);
                    return ItemStack.EMPTY;
                }
            }else return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack toExtract = items[slot].copy();
            if(isCreative)
                toExtract.setCount(amount);
            else {
                if(!simulate){
                    if(amount <= count[slot]){
                        count[slot] -= amount;
                        toExtract.setCount(amount);
                    }else {
                        toExtract.setCount(count[slot]);
                        count[slot] = 0;
                    }
                }else {
                    toExtract.setCount(Math.min(amount, count[slot]));
                }
            }
            return toExtract;
        }
        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            ItemStack stack = items[slot].copy();
            stack.setCount(count[slot]);
            return stack;
        }
        @Override
        public CompoundTag serializeNBT() {
            CompoundTag compoundTag = new CompoundTag();
            CompoundTag items = new CompoundTag();

            for(int i = 0; i < this.items.length; ++i) {
                CompoundTag bigStack = new CompoundTag();
                bigStack.put(STACK, this.items[i].serializeNBT());
                bigStack.putInt(AMOUNT,isCreative ? Integer.MAX_VALUE : count[i]);
                items.put("" + i, bigStack);
            }

            compoundTag.put(BIG_ITEMS, items);
            return compoundTag;
        }
    }
}