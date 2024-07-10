package net.smartercontraptionstorage.AddStorage.ItemHandler;

import com.buuz135.functionalstorage.block.CompactingDrawerBlock;
import com.buuz135.functionalstorage.block.SimpleCompactingDrawerBlock;
import com.buuz135.functionalstorage.block.tile.CompactingDrawerTile;
import com.buuz135.functionalstorage.block.tile.SimpleCompactingDrawerTile;
import com.buuz135.functionalstorage.inventory.CompactingInventoryHandler;
import com.buuz135.functionalstorage.util.CompactingUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.buuz135.functionalstorage.inventory.CompactingInventoryHandler.*;

public class FunctionalCompactingHandlerHelper extends StorageHandlerHelper{
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof CompactingDrawerTile || entity instanceof SimpleCompactingDrawerTile;
    }

    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {
        assert canCreateHandler(entity);
        if(entity instanceof CompactingDrawerTile)
            ((CompactingDrawerTile)entity).getHandler().deserializeNBT(handler.serializeNBT());
        else ((SimpleCompactingDrawerTile)entity).getHandler().deserializeNBT(handler.serializeNBT());
    }

    @Override
    public ItemStackHandler createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        if(entity instanceof CompactingDrawerTile)
            return new FCDrawersHandler(((CompactingDrawerTile)entity).getHandler());
        else return new FCDrawersHandler(((SimpleCompactingDrawerTile)entity).getHandler());
    }

    @Override
    public boolean allowControl(Item comparedItem) {
        return comparedItem instanceof CompactingDrawerBlock.CompactingDrawerItem;
    }

    @Override
    public boolean allowControl(Block block) {
        return block instanceof CompactingDrawerBlock || block instanceof SimpleCompactingDrawerBlock;
    }
    public static class FCDrawersHandler extends HandlerHelper {
        public final int PARENT_SLOT;
        public final int[] needed;
        public final boolean isCreative;
        public int amount;
        public FCDrawersHandler(CompactingInventoryHandler handler) {
            super(handler.getSlots());
            int slots = handler.getSlots();
            needed = new int[slots];
            amount = handler.getAmount();
            this.isCreative = handler.isCreative();
            int parent_slot = -1;
            List<CompactingUtil.Result> list = handler.getResultList();
            for(int i = 0;i < slots;i++){
                slotLimits[i] = handler.getSlotLimit(i);
                items[i] = list.get(i).getResult();
                needed[i] = list.get(i).getNeeded();
                if(parent_slot == -1 && !items[i].is(Items.AIR))
                    parent_slot = i;
            }
            if(parent_slot != -1)
                PARENT_SLOT = parent_slot;
            else PARENT_SLOT = handler.getSlots() - 1;
        }
        public boolean canInsert(int slot, @NotNull ItemStack stack) {
            if(stack.isEmpty())
                return false;
            return ItemStack.isSameItem(items[slot],stack);
        }
        public void addCountInSlot(int slot, int count){
            amount += count * needed[slot];
        }
        public int getCountInSlot(int slot){
            return amount / needed[slot];
        }
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if(canInsert(slot,stack)){
                if(isCreative)
                    return ItemStack.EMPTY;
                int slotCount = getCountInSlot(slot);
                int sumCount = slotCount + stack.getCount();
                if(sumCount >= slotLimits[slot]){
                    if(!simulate)
                        addCountInSlot(slot, slotLimits[slot] - slotCount);
                    stack.setCount(slotLimits[slot] - sumCount);
                    return stack;
                }else {
                    if(!simulate)
                        addCountInSlot(slot, sumCount - slotCount);
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
                int count = getCountInSlot(slot);
                if(!simulate){
                    if(amount <= count){
                        addCountInSlot(slot,-amount);
                        toExtract.setCount(amount);
                    }else {
                        toExtract.setCount(count);
                        addCountInSlot(slot,-count);
                    }
                }else {
                    toExtract.setCount(Math.min(amount, count));
                }
            }
            return toExtract;
        }
        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            ItemStack stack = items[slot].copy();
            stack.setCount(getCountInSlot(slot));
            return stack;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.put(PARENT, this.items[PARENT_SLOT].serializeNBT());
            compoundTag.putInt(AMOUNT,amount);
            CompoundTag items = new CompoundTag();

            for(int i = 0; i < this.items.length; ++i) {
                CompoundTag bigStack = new CompoundTag();
                bigStack.put(STACK, this.items[i].serializeNBT());
                bigStack.putInt(AMOUNT, getCountInSlot(i));
                items.put("" + i, bigStack);
            }

            compoundTag.put(BIG_ITEMS, items);
            return compoundTag;
        }
    }
}