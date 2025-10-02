package net.smartercontraptionstorage.AddStorage.ItemHandler;

import com.refinedmods.refinedstorage.api.network.energy.EnergyNetworkComponent;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.configurationcard.ConfigurationCardTarget;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.exporter.AbstractExporterBlockEntity;
import com.refinedmods.refinedstorage.common.iface.InterfaceBlockEntity;
import com.refinedmods.refinedstorage.common.importer.AbstractImporterBlockEntity;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerData;
import com.refinedmods.refinedstorage.common.support.resource.ResourceTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RSCableHandlerHelper extends StorageHandlerHelper{
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof AbstractImporterBlockEntity || entity instanceof AbstractExporterBlockEntity;
    }

    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {}

    @Override
    public @NotNull ItemStackHandler createHandler(BlockEntity entity) {
        if(canCreateHandler(entity)) {
            ResourceContainerData data = null;
            boolean importEntity = true;
            if(entity instanceof ConfigurationCardTarget target && RSCableHelper.checkUpgrades(target.getUpgrades(), Items.INSTANCE.getSpeedUpgrade())) {
                if (entity instanceof AbstractImporterBlockEntity importer)
                    data = importer.getMenuData();
                else if (entity instanceof AbstractExporterBlockEntity exporter) {
                    data = exporter.getMenuData().resourceContainerData();
                    importEntity = false;
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
    public ItemStackHandler deserialize(CompoundTag nbt, HolderLookup.Provider provider, boolean client) throws IllegalAccessException {
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

    public static class RSImportCableHandler extends ItemStackHandler implements RSCableHelper<ItemStack>{
        private final StorageNetworkComponent storage;

        private final EnergyNetworkComponent energy;

        private boolean canWork = false;

        protected RSImportCableHandler(@NotNull StorageNetworkComponent storage,@NotNull EnergyNetworkComponent energy) {
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
        public @NotNull StorageNetworkComponent getStorage() {
            return storage;
        }

        @Override
        public @NotNull EnergyNetworkComponent getEnergy() {
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
        public @NotNull List<ResourceKey> getFilter() {
            return List.of();
        }

        @Override
        public void setCanWork(boolean canWork) {
            this.canWork = canWork;
        }

        @Override
        public void setFilter(@NotNull List<InterfaceBlockEntity> interfaces) {}

        @Override
        public ResourceKey getKey(ItemStack itemStack) {
            return ItemResource.ofItemStack(itemStack);
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
        private final StorageNetworkComponent storage;

        private final  EnergyNetworkComponent energy;

        private boolean canWork = false;

        private final List<ResourceKey> filter = new ArrayList<>();

        protected RSExportCableHandler(@NotNull StorageNetworkComponent storage,@NotNull EnergyNetworkComponent energy) {
            super(storage.getAll().size());
            this.storage = storage;
            this.energy = energy;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot,@NotNull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(validSlot(slot)) {
                if(getStorage().getAll().stream().toList().get(slot).resource() instanceof ItemResource item) {
                    ItemStack stack = item.toItemStack();
                    stack.setCount((int)RSCableHelper.super.extract(stack, amount, simulate));
                    return stack.isEmpty() ? ItemStack.EMPTY : stack;
                }
            }
            return ItemStack.EMPTY;
        }

        @Override
        protected void validateSlotIndex(int slot) {
            if (!validSlot(slot))
                throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getSlots() + ")");
        }

        @Override
        public @NotNull StorageNetworkComponent getStorage() {
            return storage;
        }

        @Override
        public @NotNull EnergyNetworkComponent getEnergy() {
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
        public @NotNull List<ResourceKey> getFilter() {
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
                filter.addAll(interfaceBlock.getMenuData().filterContainerData().resources().stream().collect(
                        ArrayList::new,(list,resource) -> {
                            resource.ifPresent(value -> {
                                if(value.resource() instanceof PlatformResourceKey key && key.getResourceType() == ResourceTypes.ITEM) {
                                    list.add(value.resource());
                                }
                            });
                        },ArrayList::addAll
                ));
            });
        }

        @Override
        public ResourceKey getKey(ItemStack itemStack) {
            return ItemResource.ofItemStack(itemStack);
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
