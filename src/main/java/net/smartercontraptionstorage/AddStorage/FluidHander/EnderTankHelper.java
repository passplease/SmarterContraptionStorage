package net.smartercontraptionstorage.AddStorage.FluidHander;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.init.EnderStorageModContent;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderLiquidStorage;
import codechicken.enderstorage.tile.TileEnderTank;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EnderTankHelper extends FluidHandlerHelper{
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
    public @NotNull CompoundTag serializeNBT(IFluidHandler handler, BlockEntity entity) {
        CompoundTag nbt = new CompoundTag();
        if(canCreateHandler(entity)) {
            nbt.put("Frequency",((TileEnderTank)entity).getFrequency().writeToNBT(new CompoundTag()));
        }
        return nbt;
    }

    @Override
    public String getName() {
        return "EnderTankHelper";
    }

    @Override
    public @NotNull IFluidHandler deserialize(CompoundTag nbt, HolderLookup.Provider provider, boolean client) {
        if(nbt.contains("Frequency")) {
            Frequency frequency = new Frequency(nbt.getCompound("Frequency"));
            return new EnderStorageManagerWrap(EnderStorageManager.instance(client).getStorage(frequency, EnderLiquidStorage.TYPE));
        }
        return NULL_HANDLER;
    }

    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof TileEnderTank;
    }

    @Override
    public @NotNull IFluidHandler createHandler(BlockEntity entity) {
        IFluidHandler handler = entity.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(NULL_HANDLER);
        if(handler instanceof EnderLiquidStorage)
            return new EnderStorageManagerWrap((EnderLiquidStorage)handler);
        else return NULL_HANDLER;
    }

    @Override
    public void registerBlock(Consumer<Block> register) {
        register.accept(EnderStorageModContent.ENDER_TANK_BLOCK.get());
    }

    public static class EnderStorageManagerWrap extends EnderLiquidStorage{
        private EnderLiquidStorage storage;

        public static int HandlerReload = 0;

        private int oldReloadCount = HandlerReload;

        public EnderStorageManagerWrap(EnderLiquidStorage storage) {
            super(storage.manager,storage.freq);
            this.storage = storage;
        }

        @Override
        public void clearStorage() {
            getTank().clearStorage();
        }

        @Override
        public void loadFromTag(CompoundTag tag) {
            getTank().loadFromTag(tag);
        }

        @Override
        public CompoundTag saveToTag() {
            return getTank().saveToTag();
        }

        @Override
        public FluidStack getFluid() {
            return getTank().getFluid();
        }

        @Override
        public int getFluidAmount() {
            return getTank().getFluidAmount();
        }

        @Override
        public int getCapacity() {
            return getTank().getCapacity();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return getTank().isFluidValid(stack);
        }

        @Override
        public boolean isFluidValid(int tankId, FluidStack stack) {
            return getTank().isFluidValid(tankId, stack);
        }

        @Override
        public int getTanks() {
            return getTank().getTanks();
        }

        @Override
        public FluidStack getFluidInTank(int tankId) {
            return getTank().getFluidInTank(tankId);
        }

        @Override
        public int getTankCapacity(int tankId) {
            return getTank().getTankCapacity(tankId);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return getTank().fill(resource, action);
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            return getTank().drain(maxDrain, action);
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            return getTank().drain(resource, action);
        }

        @Override
        public void setDirty() {
            getTank().setDirty();
        }

        @Override
        public void setClean() {
            getTank().setClean();
        }

        @Override
        public int getChangeCount() {
            return getTank().getChangeCount();
        }

        public EnderLiquidStorage getTank(){
            if(oldReloadCount != HandlerReload || storage == null) {
                oldReloadCount = HandlerReload;
                storage = new EnderStorageManagerWrap(EnderStorageManager.instance(manager.client).getStorage(freq, EnderLiquidStorage.TYPE));
            }
            return storage;
        }
    }
}
