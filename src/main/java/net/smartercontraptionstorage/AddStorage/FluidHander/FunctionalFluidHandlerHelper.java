package net.smartercontraptionstorage.AddStorage.FluidHander;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.block.FluidDrawerBlock;
import com.buuz135.functionalstorage.block.tile.FluidDrawerTile;
import com.buuz135.functionalstorage.fluid.BigFluidHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Consumer;

public class FunctionalFluidHandlerHelper extends FluidHandlerHelper{
    @Deprecated
    public static final String Slot = Integer.toString(DefaultSlot);
    public static final String LOCKED = "Locked";
    public static final String VOID = "Void";
    public static final String CREATIVE = "Creative";
    @Override
    public void addStorageToWorld(BlockEntity entity, IFluidHandler tank) {
        assert canCreateHandler(entity);
        if(tank instanceof BigFluidHandler handler && entity.getLevel() != null) {
            RegistryAccess registryAccess = entity.getLevel().registryAccess();
            ((FluidDrawerTile) entity).getFluidHandler().deserializeNBT(registryAccess,handler.serializeNBT(registryAccess));
        }
    }

    @Override
    public boolean canCreateHandler(Item comparedItem) {
        return comparedItem instanceof FluidDrawerBlock.FluidDrawerItem;
    }

    @Override
    public boolean canCreateHandler(Block block) {
        return block instanceof FluidDrawerBlock;
    }

    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof FluidDrawerTile;
    }

    @Override
    public @NotNull IFluidHandler createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        return ((FluidDrawerTile)entity).getFluidHandler();
    }

    @Override
    public @NotNull CompoundTag serializeNBT(HolderLookup.Provider provider, IFluidHandler handler) {
        if(handler instanceof BigFluidHandler drawer){
            CompoundTag nbt = drawer.serializeNBT(provider);
            nbt.putBoolean(LOCKED, drawer.isDrawerLocked());
            nbt.putBoolean(VOID,drawer.isDrawerVoid());
            nbt.putBoolean(CREATIVE,drawer.isDrawerCreative());
            return nbt;
        }else return new CompoundTag();
    }

    @Override
    public String getName() {
        return "FunctionalFluidHandlerHelper";
    }

    @Override
    public @NotNull BigFluidHandler deserialize(CompoundTag nbt,HolderLookup.Provider provider) {
        BigFluidHandler drawer = new BigFluidHandler(1,1) {
            @Override
            public void onChange() {}

            boolean locked;

            @Override
            public boolean isDrawerLocked() {
                return locked;
            }

            boolean Void;

            @Override
            public boolean isDrawerVoid() {
                return Void;
            }

            private boolean creative;

            @Override
            public boolean isDrawerCreative() {
                return creative;
            }

            @Override
            public void deserializeNBT(HolderLookup.Provider provider,CompoundTag nbt) {
                super.deserializeNBT(provider,nbt);
                locked = nbt.getBoolean(LOCKED);
                Void = nbt.getBoolean(VOID);
                creative = nbt.getBoolean(CREATIVE);
            }
        };
        drawer.deserializeNBT(provider,nbt);
        return drawer;
    }

    @Override
    public void registerBlock(Consumer<Block> register) {
        register.accept(FunctionalStorage.FLUID_DRAWER_1.getBlock());
        register.accept(FunctionalStorage.FLUID_DRAWER_2.getBlock());
        register.accept(FunctionalStorage.FLUID_DRAWER_4.getBlock());
    }

    @Deprecated
    public static class FluidDrawerHandler extends FluidHelper{
        public final FluidStack filter;
        @Deprecated
        public FluidDrawerHandler(BigFluidHandler handler) {
            super(handler.getTankCapacity(DefaultSlot));
            fluid = handler.getFluidInTank(DefaultSlot);
            filter = Arrays.stream(handler.getFilterStack()).toList().get(DefaultSlot);
        }

        @Deprecated
        public FluidDrawerHandler(CompoundTag nbt){
            super(0);
            filter = FluidStack.CODEC.decode(NbtOps.INSTANCE,nbt).result().orElseThrow().getFirst();
        }

        @Override
        public boolean canFill(FluidStack fluid) {
            return FluidStack.isSameFluidSameComponents(fluid, filter) || filter.isEmpty() && (FluidStack.isSameFluidSameComponents(fluid, filter) || this.fluid.isEmpty());
        }

        public void setFluid(int amount, FluidStack stack){
            if(fluid.isEmpty())
                fluid = stack.copy();
            fluid.setAmount(amount);
        }

        @Override
        public int getAmount() {
            return fluid.getAmount();
        }

        @Override
        public CompoundTag serialize(HolderLookup.Provider provider,CompoundTag tag) {
            filter.save(provider,tag);
            return tag;
        }
    }
}