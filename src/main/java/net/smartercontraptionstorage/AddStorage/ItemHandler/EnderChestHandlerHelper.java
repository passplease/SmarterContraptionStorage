package net.smartercontraptionstorage.AddStorage.ItemHandler;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.init.EnderStorageModContent;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.enderstorage.tile.TileEnderChest;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EnderChestHandlerHelper extends StorageHandlerHelper{
    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {}

    @Override
    public boolean allowControl(Item comparedItem) {
        return false;
    }

    @Override
    public boolean allowControl(Block block) {
        return false;
    }

    @Override
    public String getName() {
        return "EnderChestHandlerHelper";
    }

    @Override
    public @NotNull ItemStackHandler deserialize(CompoundTag nbt, HolderLookup.Provider provider, boolean client){
        return new EnderChestHandler(client,new Frequency(nbt.getCompound("Frequency")));
    }

    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof TileEnderChest;
    }

    @Override
    public @NotNull ItemStackHandler createHandler(BlockEntity entity) {
        assert entity instanceof TileEnderChest;
        return new EnderChestHandler(entity.getLevel().isClientSide(),((TileEnderChest)entity).getFrequency());
    }

    @Override
    public void registerBlock(Consumer<Block> register) {
        register.accept(EnderStorageModContent.ENDER_CHEST_BLOCK.get());
    }

    public static class EnderChestHandler extends ItemStackHandler {
        protected final boolean client;
        protected final Frequency frequency;

        private IItemHandler handler;

        public static int HandlerReload = 0;

        private int oldReloadCount = HandlerReload;

        public EnderChestHandler(boolean client, Frequency frequency) {
            stacks = NonNullList.withSize(getHandler().getSlots(), ItemStack.EMPTY);
            this.client = client;
            this.frequency = frequency;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return getHandler().extractItem(slot, amount, simulate);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return getHandler().insertItem(slot, stack, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return getHandler().getSlotLimit(slot);
        }

        @Override
        public int getSlots() {
            return getHandler().getSlots();
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            InvWrapper handler = getHandler();
            handler.extractItem(slot,Integer.MAX_VALUE,false);
            handler.insertItem(slot,stack,false);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return getHandler().isItemValid(slot, stack);
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return getHandler().getStackInSlot(slot);
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            tag.put("Frequency",frequency.writeToNBT(new CompoundTag()));
            return tag;
        }

        public InvWrapper getHandler(){
            if(oldReloadCount != HandlerReload || handler == null) {
                oldReloadCount = HandlerReload;
                handler = new InvWrapper(EnderStorageManager.instance(client).getStorage(frequency, EnderItemStorage.TYPE));
            }
            return (InvWrapper) handler;
        }
    }
}
