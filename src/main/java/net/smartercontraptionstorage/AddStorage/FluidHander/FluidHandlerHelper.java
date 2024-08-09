package net.smartercontraptionstorage.AddStorage.FluidHander;

import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.smartercontraptionstorage.AddStorage.MenuSupportHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public abstract class FluidHandlerHelper implements MenuSupportHandler {
    /**
     * Due to we can only add one tank to the contraption for each entity, so we add DefaultSlot of muti-slots entity
     * */
    public static final int DefaultSlot = 0;
    public static boolean canUseAsStorage(@NotNull Item comparedItem){
        return canUseAsStorage(comparedItem,Block.byItem(comparedItem));
    }
    public static boolean canUseAsStorage(@NotNull Block comparedBlock){
        return canUseAsStorage(comparedBlock.asItem(),comparedBlock);
    }
    private static final Set<FluidHandlerHelper> HandlerHelpers = new HashSet<>();
    public static void register(@NotNull FluidHandlerHelper handlerHelper){
        HandlerHelpers.add(handlerHelper);
    }
    public static boolean canUseAsStorage(@Nullable Item comparedItem, @Nullable Block comparedBlock){
        if(comparedItem == null || comparedBlock == null)
            return false;
        for(FluidHandlerHelper handlerHelper : HandlerHelpers)
            if(handlerHelper.canCreateHandler(comparedItem))
                return true;
        if(comparedBlock == Blocks.AIR)
            return false;
        for(FluidHandlerHelper handlerHelper : HandlerHelpers)
            if(handlerHelper.canCreateHandler(comparedBlock))
                return true;
        return false;
    }
    public static boolean canUseAsStorage(BlockEntity entity){
        for(FluidHandlerHelper handlerHelper : HandlerHelpers)
            if(handlerHelper.canCreateHandler(entity))
                return true;
        return false;
    }
    public static @Nullable FluidHandlerHelper findSuitableHelper(BlockEntity entity){
        for(FluidHandlerHelper handlerHelper : HandlerHelpers)
            if(handlerHelper.canCreateHandler(entity))
                return handlerHelper;
        return null;
    }
    public boolean sendPacket(){
        return false;
    }
    public void tick(Entity entity, BlockPos pos, boolean isRemote){}
    public abstract void addStorageToWorld(BlockEntity entity,SmartFluidTank helper);
    public abstract boolean canCreateHandler(Item comparedItem);
    public abstract boolean canCreateHandler(Block block);
    public abstract boolean canCreateHandler(BlockEntity entity);
    public abstract @Nullable SmartFluidTank createHandler(BlockEntity entity);
    public static Set<FluidHandlerHelper> getHandlerHelpers() {
        return HandlerHelpers;
    }
    public static abstract class FluidHelper extends SmartFluidTank implements INBTSerializable<CompoundTag> {
        public FluidHelper(int capacity){
            super(capacity,null);
        }
        public abstract boolean canFill(FluidStack fluid);
        public abstract void setFluid(int amount,FluidStack stack);
        @Override
        public int fill(FluidStack resource, FluidAction action){
            if(canFill(resource)){
                int sum = resource.getAmount() + getAmount();
                if(sum <= capacity){
                    if(action.execute())
                        setFluid(sum,resource);
                    return resource.getAmount();
                }else {
                    sum = capacity - getAmount();
                    if(action.execute())
                        setFluid(capacity,resource);
                    return sum;
                }
            }else return 0;
        }
        public abstract int getAmount();
        @Override
        public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action){
            FluidStack toExtract = fluid.copy();
            toExtract.setAmount(Math.min(maxDrain,getAmount()));
            if(action.execute())
                setFluid(getAmount() - toExtract.getAmount(),fluid);
            return toExtract;
        }
        @Override
        public void deserializeNBT(CompoundTag tag) {}
        @Override
        public int getFluidAmount() {
            return getAmount();
        }
    }
}