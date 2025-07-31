package net.smartercontraptionstorage.AddStorage.FluidHander;

import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.network.node.ExporterNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.ImporterNetworkNode;
import com.refinedmods.refinedstorage.blockentity.ExporterBlockEntity;
import com.refinedmods.refinedstorage.blockentity.ImporterBlockEntity;
import com.refinedmods.refinedstorage.blockentity.InterfaceBlockEntity;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.smartercontraptionstorage.AddStorage.ItemHandler.RSCableHandlerHelper;
import net.smartercontraptionstorage.AddStorage.ItemHandler.RSCableHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class RSCableFluidHelper extends FluidHandlerHelper{
    @Override
    public void addStorageToWorld(BlockEntity entity, IFluidHandler tank) {}

    @Override
    public boolean canCreateHandler(Item comparedItem) {
        return false;
    }

    @Override
    public boolean canCreateHandler(Block block) {
        return false;
    }

    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof ImporterBlockEntity || entity instanceof ExporterBlockEntity;
    }

    @Override
    public @NotNull IFluidHandler createHandler(BlockEntity entity) {
        if(canCreateHandler(entity)) {
            IItemHandlerModifiable data = null;
            boolean importEntity = true;
            if(entity instanceof NetworkNodeBlockEntity<?>) {
                if (entity instanceof ImporterBlockEntity importer) {
                    ImporterNetworkNode node = importer.getNode();
                    if(RSCableHelper.checkUpgrades(node.getUpgrades(), RSItems.UPGRADE_ITEMS.get(UpgradeItem.Type.SPEED).get()))
                        data = node.getItemFilters();
                } else if (entity instanceof ExporterBlockEntity exporter) {
                    ExporterNetworkNode node = exporter.getNode();
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
    public @NotNull CompoundTag serializeNBT(IFluidHandler handler) {
        return new CompoundTag();
    }

    @Override
    public String getName() {
        return "RSCableFluidHelper";
    }

    @Override
    public @NotNull IFluidHandler deserialize(CompoundTag nbt, HolderLookup.Provider provider) throws IllegalAccessException {
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

    public static class RSImportCableHandler extends FluidTank implements RSCableHelper<FluidStack>{
        private final INetwork storage;

        private final IEnergyStorage energy;

        private boolean canWork = false;

        protected RSImportCableHandler(@NotNull INetwork storage, @NotNull IEnergyStorage energy) {
            super(1);
            this.storage = storage;
            this.energy = energy;
        }

        @Override
        public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
            return (int) RSCableHelper.super.insert(resource, resource.getAmount(), action.simulate());
        }

        @Override
        public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
            return FluidStack.EMPTY;
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
            return FluidStack.EMPTY;
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
        public @NotNull List<FluidStack> getFilter() {
            return List.of();
        }

        @Override
        public void setCanWork(boolean canWork) {
            this.canWork = canWork;
        }

        @Override
        public void setFilter(@NotNull List<InterfaceBlockEntity> interfaces) {}

        @Override
        public long insert(@NotNull FluidStack fluidStack, int amount, Action action) {
            return amount - getStorage().insertFluid(fluidStack, amount, action).getAmount();
        }

        @Override
        public long extract(@NotNull FluidStack fluidStack, int amount, int flags, Action action) {
            return 0;
        }

        @Override
        public boolean isItemHandler() {
            return false;
        }

        @Override
        public boolean isFluidHandler() {
            return true;
        }
    }

    public static class RSExportCableHandler extends FluidTank implements RSCableHelper<FluidStack>{
        private final INetwork storage;

        private final IEnergyStorage energy;

        private boolean canWork = false;

        private final List<FluidStack> filter = new ArrayList<>();

        protected RSExportCableHandler(@NotNull INetwork storage,@NotNull IEnergyStorage energy) {
            super(storage.getFluidStorageCache().getList().getStacks().size());
            this.storage = storage;
            this.energy = energy;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            FluidStack stack = RSCableHelper.super.getFluidInTank(tank);
            return !stack.isEmpty() && getFilter().contains(stack) ? stack : FluidStack.EMPTY;
        }

        @Override
        public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
            return 0;
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
            FluidStack fluid = getFluidInTank(0);
            fluid.setAmount(maxDrain);
            return drain(fluid, action);
        }

        @Override
        public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
            FluidStack stack = new FluidStack(resource, (int) RSCableHelper.super.extract(resource, resource.getAmount(), action.simulate()));
            return stack.isEmpty() ? FluidStack.EMPTY : stack;
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
        public @NotNull List<FluidStack> getFilter() {
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
                        FluidUtil.getFluidContained(items.getStackInSlot(slot)).ifPresent(filter::add);
                    }
                });
            });
        }

        @Override
        public long insert(@NotNull FluidStack fluidStack, int amount, Action action) {
            return 0;
        }

        @Override
        public long extract(@NotNull FluidStack fluidStack, int amount, int flags, Action action) {
            return amount - getStorage().extractFluid(fluidStack, amount, flags, action).getAmount();
        }

        @Override
        public boolean isItemHandler() {
            return false;
        }

        @Override
        public boolean isFluidHandler() {
            return true;
        }
    }
}
