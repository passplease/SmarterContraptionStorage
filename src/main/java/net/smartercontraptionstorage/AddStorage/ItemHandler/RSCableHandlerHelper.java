package net.smartercontraptionstorage.AddStorage.ItemHandler;

import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.network.node.ExporterNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.ImporterNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.blockentity.ExporterBlockEntity;
import com.refinedmods.refinedstorage.blockentity.ImporterBlockEntity;
import com.refinedmods.refinedstorage.blockentity.InterfaceBlockEntity;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Deprecated
public class RSCableHandlerHelper extends StorageHandlerHelper{
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof ImporterBlockEntity || entity instanceof ExporterBlockEntity;
    }

    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {}

    @Override
    public @NotNull ItemStackHandler createHandler(BlockEntity entity) {
        if(canCreateHandler(entity)) {
            IItemHandlerModifiable data = null;
            boolean importEntity = true;
            if(entity instanceof NetworkNodeBlockEntity<?>) {
                NetworkNode n = ((ImporterBlockEntity) entity).getNode();
                if (n instanceof ImporterNetworkNode node) {
                    if(RSCableHelper.checkUpgrades(node.getUpgrades(),RSItems.UPGRADE_ITEMS.get(UpgradeItem.Type.SPEED).get()))
                        data = node.getItemFilters();
                } else if (n instanceof ExporterNetworkNode node) {
                    if(RSCableHelper.checkUpgrades(node.getUpgrades(),RSItems.UPGRADE_ITEMS.get(UpgradeItem.Type.SPEED).get())) {
                        data = node.getItemFilters();
                        importEntity = false;
                    }
                }
                if(data != null && entity.getLevel() != null && entity.getLevel().getServer() != null){
                    return RSCableHelper.create(data,entity.getLevel().getServer(),NULL_HANDLER,importEntity ? RSImportCableHandler::new: RSExportCableHandler::new);
                }
            }
        }
        return NULL_HANDLER;
    }

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
        return "RSCableHandlerHelper";
    }

    @Override
    public @NotNull ItemStackHandler deserialize(CompoundTag nbt, HolderLookup.Provider provider, boolean client) throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    @Override
    public boolean canDeserialize() {
        return false;
    }

    @Override
    public void registerBlock(Consumer<Block> register) {
        RSCableHelper.register(register);
    }

    @Deprecated
    public static class RSImportCableHandler extends ItemStackHandler implements RSCableHelper<ItemStack>{
        private final INetwork storage;

        private final IEnergyStorage energy;

        private boolean canWork = false;

        protected RSImportCableHandler(@NotNull INetwork storage, @NotNull IEnergyStorage energy) {
            super(1);
            this.storage = storage;
            this.energy = energy;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            stack.shrink((int) RSCableHelper.super.insert(stack,stack.getCount(),simulate));
            return stack.isEmpty() ? ItemStack.EMPTY : stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public @NotNull INetwork getStorage() {
            return storage;
        }

        @Override
        public @NotNull IEnergyStorage getEnergy() {
            return energy;
        }

        @Override
        public boolean canWork() {
            return canWork;
        }

        @Override
        public boolean hasFilter() {
            return false;
        }

        @Override
        public @NotNull List<ItemStack> getFilter() {
            return List.of();
        }

        @Override
        public void setCanWork(boolean canWork) {
            this.canWork = canWork;
        }

        @Override
        public void setFilter(@NotNull List<InterfaceBlockEntity> interfaces) {}

        @Override
        public long insert(@NotNull ItemStack itemStack, int amount, Action action) {
            return amount - getStorage().insertItem(itemStack, amount, action).getCount();
        }

        @Override
        public long extract(@NotNull ItemStack itemStack, int amount, int flags, Action action) {
            return 0;
        }

        @Override
        public boolean isItemHandler() {
            return true;
        }

        @Override
        public boolean isFluidHandler() {
            return false;
        }
    }

    public static class RSExportCableHandler extends ItemStackHandler implements RSCableHelper<ItemStack>{
        private final INetwork storage;

        private final  IEnergyStorage energy;

        private boolean canWork = false;

        private final List<ItemStack> filter = new ArrayList<>();

        protected RSExportCableHandler(@NotNull INetwork storage,@NotNull IEnergyStorage energy) {
            super(storage.getItemStorageCache().getList().getStacks().size());
            this.storage = storage;
            this.energy = energy;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot,@NotNull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack stack = getStackInSlot(slot).copy();
            stack.setCount((int) RSCableHelper.super.extract(stack,stack.getCount(),simulate));
            return stack.isEmpty() ? ItemStack.EMPTY : stack;
        }

        @Override
        protected void validateSlotIndex(int slot) {
            if (!validSlot(slot))
                throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getSlots() + ")");
        }

        @Override
        public @NotNull INetwork getStorage() {
            return storage;
        }

        @Override
        public @NotNull IEnergyStorage getEnergy() {
            return energy;
        }

        @Override
        public boolean canWork() {
            return canWork;
        }

        @Override
        public boolean hasFilter() {
            return true;
        }

        @Override
        public @NotNull List<ItemStack> getFilter() {
            return filter;
        }

        @Override
        public void setCanWork(boolean canWork) {
            this.canWork = canWork;
        }

        @Override
        public void setFilter(@NotNull List<InterfaceBlockEntity> interfaces) {
            filter.clear();
            interfaces.forEach(interfaceBlock -> {
                interfaceBlock.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(items -> {
                    for (int slot = 0; slot < items.getSlots(); slot++) {
                        filter.add(items.getStackInSlot(slot));
                    }
                });
            });
        }

        @Override
        public long insert(@NotNull ItemStack itemStack, int amount, Action action) {
            return 0;
        }

        @Override
        public long extract(@NotNull ItemStack stack, int amount, int flags, Action action) {
            return amount - getStorage().extractItem(stack,amount,flags,action).getCount();
        }

        @Override
        public boolean isItemHandler() {
            return true;
        }

        @Override
        public boolean isFluidHandler() {
            return false;
        }
    }
}
