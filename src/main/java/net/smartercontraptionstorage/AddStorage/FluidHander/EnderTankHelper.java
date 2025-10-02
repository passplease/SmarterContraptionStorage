package net.smartercontraptionstorage.AddStorage.FluidHander;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.init.EnderStorageModContent;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderLiquidStorage;
import codechicken.enderstorage.tile.TileEnderTank;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EnderTankHelper extends FluidHandlerHelper{
    @Override
    public void addStorageToWorld(BlockEntity entity, IFluidHandler tank) {}

    @Override
    public boolean canCreateHandler(Item comparedItem) {
        return comparedItem == EnderStorageModContent.ENDER_TANK_ITEM.get();
    }

    @Override
    public boolean canCreateHandler(Block block) {
        return block == EnderStorageModContent.ENDER_TANK_BLOCK.get();
    }

    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof TileEnderTank;
    }

    @Override
    public @NotNull IFluidHandler createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        IFluidHandler handler = ((TileEnderTank)entity).getFluidHandler();
        if(handler instanceof EnderLiquidStorage)
            return new EnderStorageManagerWrap((EnderLiquidStorage)handler);
        else return NULL_HANDLER;
    }

    @Override
    public @NotNull CompoundTag serializeNBT(HolderLookup.Provider provider, IFluidHandler handler) {
        CompoundTag nbt = new CompoundTag();
        if(handler instanceof EnderLiquidStorage){
            nbt.put("Frequency", Frequency.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), ((EnderLiquidStorage) handler).freq).getOrThrow());
        }
        return nbt;
    }

    @Override
    public String getName() {
        return "EnderTankHelper";
    }

    @Override
    public IFluidHandler deserialize(CompoundTag nbt, HolderLookup.Provider provider, boolean client) throws IllegalAccessException {
        if(nbt.contains("Frequency")) {
            Frequency frequency = new Frequency(nbt.getCompound("Frequency"));
            return new EnderStorageManagerWrap(EnderStorageManager.instance(client).getStorage(frequency, EnderLiquidStorage.TYPE));
        }
        return NULL_HANDLER;
    }

    @Override
    public void registerBlock(Consumer<Block> register) {
        register.accept(EnderStorageModContent.ENDER_TANK_BLOCK.get());
    }

    public static class EnderStorageManagerWrap extends EnderLiquidStorage {
        private EnderLiquidStorage storage;

        public static int HandlerReload = 0;

        private int oldReloadCount = HandlerReload;

        public EnderStorageManagerWrap(EnderLiquidStorage storage) {
            super(storage.manager, storage.freq);
            this.storage = storage;
        }

        @Override
        public void clearStorage() {
            getTank().clearStorage();
        }

        @Override
        public void loadFromTag(CompoundTag tag, HolderLookup.Provider registries) {
            getTank().loadFromTag(tag, registries);
        }

        @Override
        public CompoundTag saveToTag(HolderLookup.Provider registries) {
            return getTank().saveToTag(registries);
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
        public @NotNull FluidStack getFluidInTank(int tankId) {
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
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return getTank().drain(maxDrain, action);
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
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

        public EnderLiquidStorage getTank() {
            if (oldReloadCount != HandlerReload || storage == null) {
                oldReloadCount = HandlerReload;
                storage = new EnderStorageManagerWrap(EnderStorageManager.instance(manager.client).getStorage(freq, EnderLiquidStorage.TYPE));
            }
            return storage;
        }
    }
}
