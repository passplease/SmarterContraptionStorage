package net.smartercontraptionstorage.AddStorage.FluidHander;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKey;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.core.definitions.AEBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.smartercontraptionstorage.AddStorage.ItemHandler.AE2BusBlockHelper;
import net.smartercontraptionstorage.AddStorage.ItemHandler.AE2BusHelper;
import net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;

public class AE2BusBlockFluidHelper extends FluidHandlerHelper{
    @Override
    public void addStorageToWorld(BlockEntity entity, IFluidHandler tank) {}

    @Deprecated
    @Override
    public boolean canCreateHandler(Item comparedItem) {
        return false;
    }

    @Deprecated
    @Override
    public boolean canCreateHandler(Block block) {
        return false;
    }

    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof CableBusBlockEntity;
    }

    @Override
    public @NotNull IFluidHandler createHandler(BlockEntity entity) {
        assert this.canCreateHandler(entity);
        return AE2BusHelper.createHandler(entity,NULL_HANDLER,AE2HandlerHelper::new);
    }

    @Override
    public @NotNull CompoundTag serializeNBT(HolderLookup.Provider provider, IFluidHandler handler) {
        return ((AE2BusHelper)handler).serializeNBT(provider,new CompoundTag());
    }

    @Override
    public String getName() {
        return "AE2BusBlockFluidHelper";
    }

    @Override
    public IFluidHandler deserialize(CompoundTag nbt, HolderLookup.Provider provider) throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    @Override
    public void registerBlock(Consumer<Block> register) {
        AE2BusHelper.registerBlock(register);
    }

    @Override
    public boolean canDeserialize() {
        return false;
    }

    public static class AE2HandlerHelper extends FluidTank implements AE2BusHelper {
        protected final @Nullable IGridNode extractNode;
        protected final @Nullable IGridNode importNode;
        private final ArrayList<AEKey> extractKeys = new ArrayList<>();
        private boolean hasFilter = false;
        private boolean canWork = false;
        private final AE2ContraptionSource extractSource;
        private final AE2ContraptionSource importSource;

        protected AE2HandlerHelper(int size, @Nullable IGridNode extractNode, @Nullable IGridNode importNode) {
            super(size);
            this.extractNode = extractNode;
            this.importNode = importNode;
            this.extractSource = AE2ContraptionSource.create(extractNode);
            this.importSource = AE2ContraptionSource.create(importNode);
        }

        @Override
        public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
            return Math.toIntExact(AE2BusHelper.super.insert(AEFluidKey.of(resource),resource.getAmount(), Actionable.of(action)));
        }

        @Override
        public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
            long extracted = AE2BusHelper.super.extract(AEFluidKey.of(resource), resource.getAmount(), Actionable.of(action));
            return extracted <= 0 ? FluidStack.EMPTY : resource.copyWithAmount((int)extracted);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
            FluidStack fluid = getFluidInTank(0);
            fluid.setAmount(maxDrain);
            return drain(fluid,action);
        }

        @Override
        public boolean isItemHandler() {
            return false;
        }

        @Override
        public boolean isFluidHandler() {
            return true;
        }

        @Override
        public boolean getCanWork() {
            if(getExtractNode() == null && getImportNode() == null)
                canWork = false;
            return canWork;
        }

        @Override
        public void setCanWork(boolean canWork) {
            if(getExtractNode() == null && getImportNode() == null)
                canWork = false;
            this.canWork = canWork;
        }

        @Override
        public boolean hasFilter() {
            return hasFilter;
        }

        @Override
        public void setFilter(boolean hasFilter) {
            this.hasFilter = hasFilter;
        }

        @Override
        public @Nullable IGridNode getExtractNode() {
            return extractNode != null && extractNode.isActive() ? extractNode : null;
        }

        @Override
        public @Nullable IGridNode getImportNode() {
            return importNode != null  && importNode.isActive() ? importNode : null;
        }

        @Override
        public ArrayList<AEKey> getExtractKeys() {
            return extractKeys;
        }

        @Override
        public AE2ContraptionSource getExtractSource() {
            return extractSource;
        }

        @Override
        public AE2ContraptionSource getImportSource() {
            return importSource;
        }
    }
}
