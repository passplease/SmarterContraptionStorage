package net.smartercontraptionstorage.AddStorage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public abstract class StorageHandlerHelper {
    public static final Set<StorageHandlerHelper> HandlerHelpers = new HashSet<>();
    public static void register(@NotNull StorageHandlerHelper helper){
        HandlerHelpers.add(helper);
    }
    public static boolean canControl(Item comparedItem){
        return canControl(comparedItem,Block.byItem(comparedItem));
    }
    public static boolean canControl(Block comparedBlock){
        return canControl(comparedBlock.asItem(),comparedBlock);
    }
    private static boolean canControl(@Nullable Item comparedItem,@Nullable Block comparedBlock){
        if(comparedItem == null || comparedBlock == null)
            return false;
        for (StorageHandlerHelper handlerHelper : HandlerHelpers) {
            if (handlerHelper.allowControl(comparedItem))
                return true;
        }
        if(comparedBlock == Blocks.AIR)
            return false;
        for (StorageHandlerHelper handlerHelper : HandlerHelpers){
            if( handlerHelper.allowControl(comparedBlock))
                return true;
        }
        return false;
    }
    public static boolean canUseModdedInventory(BlockEntity entity){
        for(StorageHandlerHelper handlerHelper : HandlerHelpers){
            if(handlerHelper.canCreateHandler(entity))
                return true;
        }
        return false;
    }
    public static @Nullable StorageHandlerHelper findSuitableHelper(BlockEntity entity){
        for(StorageHandlerHelper handlerHelper : HandlerHelpers)
            if(handlerHelper.canCreateHandler(entity))
                return handlerHelper;
        return null;
    }
    public abstract boolean canCreateHandler(BlockEntity entity);
    public abstract void addStorageToWorld(BlockEntity entity,ItemStackHandler handler);
    public abstract ItemStackHandler createHandler(BlockEntity entity);
    public CompoundTag serialize(CompoundTag tag){return tag;}
    public abstract boolean allowControl(Item comparedItem);
    public abstract boolean allowControl(Block block);
    public abstract static class HandlerHelper extends ItemStackHandler{
        public final int[] slotLimits;
        protected final ItemStack[] items;
        public HandlerHelper(int size) {
            super(size);
            slotLimits = new int[size];
            items = new ItemStack[size];
        }
        @Override
        public int getSlots() {
            return items.length;
        }
        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return items[slot];
        }
        @Override
        public int getStackLimit(int slot, @NotNull ItemStack stack){
            if(ItemStack.isSameItem(stack,items[slot]))
                return slotLimits[slot];
            else return 0;
        }
        @Override
        public int getSlotLimit(int slot) {
            return slotLimits[slot];
        }
        @Override
        public abstract @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate);
        @Override
        public abstract @NotNull ItemStack extractItem(int slot, int amount, boolean simulate);
    }
}