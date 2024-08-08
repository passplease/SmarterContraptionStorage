package net.smartercontraptionstorage.AddStorage.ItemHandler;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.smartercontraptionstorage.AddStorage.MenuSupportHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class StorageHandlerHelper implements MenuSupportHandler {
    public static final ItemStackHandler nullHandler = new ItemStackHandler(){
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return false;
        }
    };
    private static final Set<StorageHandlerHelper> HandlerHelpers = new HashSet<>();
    protected static final ArrayList<BlockEntity> BlockEntityList = new ArrayList<>();
    public static void clearData(){
        BlockEntityList.clear();
    }
    public static void register(@NotNull StorageHandlerHelper helper){
        HandlerHelpers.add(helper);
    }
    public static boolean canControl(@Nullable Item comparedItem){
        return canControl(comparedItem,Block.byItem(comparedItem));
    }
    public static boolean canControl(@NotNull Block comparedBlock){
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
    public abstract @NotNull ItemStackHandler createHandler(BlockEntity entity);
    public abstract boolean allowControl(Item comparedItem);
    public abstract boolean allowControl(Block block);
    // two allowDumping only need to achieve one, another can return false
    public static Set<StorageHandlerHelper> getHandlerHelpers() {
        return HandlerHelpers;
    }

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
            if(items[slot].sameItem(stack))
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