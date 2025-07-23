package net.smartercontraptionstorage.AddStorage.FluidHander;

import com.refinedmods.refinedstorage.api.network.energy.EnergyNetworkComponent;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.configurationcard.ConfigurationCardTarget;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.content.Blocks;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.exporter.AbstractExporterBlockEntity;
import com.refinedmods.refinedstorage.common.iface.InterfaceBlockEntity;
import com.refinedmods.refinedstorage.common.importer.AbstractImporterBlockEntity;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerData;
import com.refinedmods.refinedstorage.common.support.resource.ResourceTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.smartercontraptionstorage.AddStorage.ItemHandler.RSCableHandlerHelper;
import net.smartercontraptionstorage.AddStorage.ItemHandler.RSCableHelper;
import net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper;
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
        return entity instanceof AbstractImporterBlockEntity || entity instanceof AbstractExporterBlockEntity;
    }

    @Override
    public @NotNull IFluidHandler createHandler(BlockEntity entity) {
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
                    return RSCableHelper.create(data,entity.getLevel().getServer(),NULL_HANDLER,importEntity ? RSImportCableHandler::new : RSExportCableHandler::new);
                }
            }
        }
        return NULL_HANDLER;
    }

    @Override
    public @NotNull CompoundTag serializeNBT(HolderLookup.Provider provider, IFluidHandler handler) {
        return new CompoundTag();
    }

    @Override
    public String getName() {
        return "RSCableFluidHelper";
    }

    @Override
    public IFluidHandler deserialize(CompoundTag nbt, HolderLookup.Provider provider) throws IllegalAccessException {
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
        private final StorageNetworkComponent storage;

        private final EnergyNetworkComponent energy;

        private boolean canWork = false;

        protected RSImportCableHandler(@NotNull StorageNetworkComponent storage,@NotNull EnergyNetworkComponent energy) {
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
        public ResourceKey getKey(FluidStack fluidStack) {
            return new FluidResource(fluidStack.getFluid(),fluidStack.getComponentsPatch());
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
        private final StorageNetworkComponent storage;

        private final EnergyNetworkComponent energy;

        private boolean canWork = false;

        private final List<ResourceKey> filter = new ArrayList<>();

        protected RSExportCableHandler(@NotNull StorageNetworkComponent storage,@NotNull EnergyNetworkComponent energy) {
            super(storage.getAll().size());
            this.storage = storage;
            this.energy = energy;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            Optional<ResourceAmount> optional = getStorage().getAll().stream()
                    .filter(resource -> resource.resource() instanceof FluidResource && allowedKey(resource.resource()))
                    .findAny();
            return optional.map(resourceAmount -> new FluidStack(((FluidResource) resourceAmount.resource()).fluid(), (int) resourceAmount.amount())).orElse(FluidStack.EMPTY);
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
            FluidResource fluidResource = new FluidResource(resource.getFluid(), resource.getComponentsPatch());
            Optional<ResourceKey> key = getStorage().getAll().stream().map(ResourceAmount::resource).filter(fluidResource::equals).findFirst();
            if(key.isPresent()) {
                long extracted = RSCableHelper.super.extract(resource, resource.getAmount(), action.simulate());
                if(extracted > 0) {
                    return resource.copyWithAmount((int) extracted);
                }
            }
            return FluidStack.EMPTY;
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
                                if(value.resource() instanceof PlatformResourceKey key && key.getResourceType() == ResourceTypes.FLUID) {
                                    list.add(value.resource());
                                }
                            });
                        },ArrayList::addAll
                ));
            });
        }

        @Override
        public ResourceKey getKey(FluidStack fluidStack) {
            return new FluidResource(fluidStack.getFluid(),fluidStack.getComponentsPatch());
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
