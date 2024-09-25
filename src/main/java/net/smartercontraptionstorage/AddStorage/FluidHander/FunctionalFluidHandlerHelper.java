package net.smartercontraptionstorage.AddStorage.FluidHander;

import com.buuz135.functionalstorage.block.FluidDrawerBlock;
import com.buuz135.functionalstorage.block.tile.FluidDrawerTile;
import com.buuz135.functionalstorage.fluid.BigFluidHandler;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class FunctionalFluidHandlerHelper extends FluidHandlerHelper{
    public static final String Slot = Integer.toString(DefaultSlot);
    public static final String Locked = "Locked" + DefaultSlot;
    @Override
    public void addStorageToWorld(BlockEntity entity, SmartFluidTank helper) {
        assert canCreateHandler(entity);
        FluidDrawerTile Entity = (FluidDrawerTile)entity;
        CompoundTag nbt = Entity.getFluidHandler().serializeNBT();
        nbt.put(Slot, helper.getFluid().writeToNBT(new CompoundTag()));
        nbt.put(Locked,((FluidDrawerHandler)helper).filter.writeToNBT(new CompoundTag()));
        Entity.getFluidHandler().deserializeNBT(nbt);
    }

    @Override
    public boolean canCreateHandler(Item comparedItem) {
        return comparedItem instanceof FluidDrawerBlock.DrawerItem;
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
    public @NotNull SmartFluidTank createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        return new FluidDrawerHandler(((FluidDrawerTile) entity).getFluidHandler());
    }

    @Override
    public String getName() {
        return "FunctionalFluidHandlerHelper";
    }

    @Override
    public @NotNull FluidDrawerHandler deserialize(CompoundTag nbt) throws IllegalAccessException {
        return new FluidDrawerHandler(nbt);
    }

    public static class FluidDrawerHandler extends FluidHelper{
        public final FluidStack filter;
        public FluidDrawerHandler(BigFluidHandler handler) {
            super(handler.getTankCapacity(DefaultSlot));
            fluid = handler.getFluidInTank(DefaultSlot);
            filter = Arrays.stream(handler.getFilterStack()).toList().get(DefaultSlot);
        }

        public FluidDrawerHandler(CompoundTag nbt){
            super(nbt);
            filter = FluidStack.loadFluidStackFromNBT(nbt);
        }

        @Override
        public boolean canFill(FluidStack fluid) {
            return filter.isFluidEqual(fluid) || filter.isEmpty() && (this.fluid.isFluidEqual(fluid) || this.fluid.isEmpty());
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
        public CompoundTag serialize(CompoundTag tag) {
            filter.writeToNBT(tag);
            return tag;
        }
    }
}