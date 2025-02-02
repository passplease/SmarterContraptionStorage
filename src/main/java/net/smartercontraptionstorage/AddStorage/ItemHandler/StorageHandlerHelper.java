package net.smartercontraptionstorage.AddStorage.ItemHandler;

import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.smartercontraptionstorage.AddStorage.MenuSupportHandler;
import net.smartercontraptionstorage.AddStorage.SerializableHandler;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class StorageHandlerHelper implements MenuSupportHandler, SerializableHandler {
    public static final String DESERIALIZE_MARKER = "OtherHandlers";
    public static final ItemStackHandler NULL_HANDLER = new ItemStackHandler(){
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
    public static StorageHandlerHelper findByName(String name){
        return HandlerHelpers.stream().filter((helper)-> helper.canDeserialize() && Objects.equals(helper.getName(), name)).findFirst().orElse(null);
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

    public abstract static class HandlerHelper extends ItemStackHandler {
        public final int[] slotLimits;
        protected final ItemStack[] items;
        public HandlerHelper(int size) {
            super(size);
            slotLimits = new int[size];
            items = new ItemStack[size];
        }
        protected HandlerHelper(CompoundTag nbt){
            super(nbt.getInt("size"));
            ListTag list_slotLimits = nbt.getList("slotLimits", Tag.TAG_INT);
            List<ItemStack> list_items = NBTHelper.readItemList(nbt.getList("items", Tag.TAG_COMPOUND));
            int size = list_items.size();
            slotLimits = new int[size];
            items = new ItemStack[size];
            for (int slot = 0; slot < size; slot++) {
                slotLimits[slot] = list_slotLimits.getInt(slot);
                items[slot] = list_items.get(slot);
            }
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
            if(Utils.isSameItem(items[slot],stack))
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
        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            ListTag list = new ListTag(),itemList = new ListTag();
            for (int slot = 0; slot < slotLimits.length; slot++) {
                list.add(IntTag.valueOf(slotLimits[slot]));
                itemList.add(items[slot].serializeNBT());
            }
            tag.put("slotLimits",list);
            tag.put("items",itemList);
            tag.putInt("size",slotLimits.length);
            return tag;
        }
        public abstract String getName();
        protected boolean isItemEmpty(int slot){
            return items[slot].isEmpty() || items[slot].getItem() == Items.AIR;
        }
    }
}