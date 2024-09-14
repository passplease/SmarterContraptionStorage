package net.smartercontraptionstorage.Mixin.Storage;

import com.simibubi.create.content.contraptions.MountedFluidStorage;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.smartercontraptionstorage.AddStorage.FluidHander.FluidHandlerHelper;
import net.smartercontraptionstorage.AddStorage.NeedDealWith;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MountedFluidStorage.class)
public class MountedFluidStorageMixin {
    @Shadow(remap = false)
    SmartFluidTank tank;
    @Shadow(remap = false) private boolean valid;
    @Shadow(remap = false) private boolean sendPacket;
    @Shadow private BlockEntity blockEntity;
    @Unique @Nullable
    FluidHandlerHelper smarterContraptionStorage$handlerHelper;
    @Unique boolean smarterContraptionStorage$canUseForStorage;
    @Inject(method = "canUseAsStorage",at = @At("RETURN"),remap = false, cancellable = true)
    private static void canUseAsStorage(BlockEntity be, CallbackInfoReturnable<Boolean> cir){
        if(!cir.getReturnValue())
            cir.setReturnValue(FluidHandlerHelper.canUseAsStorage(be));
    }
    @Inject(method = "assignBlockEntity",at = @At("HEAD"),remap = false)
    public void init(BlockEntity be, CallbackInfo ci){
        smarterContraptionStorage$canUseForStorage = Utils.smarterContraptionStorage$canUseForStorage;
        Utils.smarterContraptionStorage$canUseForStorage = true;
    }
    @Inject(method = "createMountedTank",at = @At("RETURN"),remap = false, cancellable = true)
    public void createMountedTank(BlockEntity be, CallbackInfoReturnable<SmartFluidTank> cir){
        if(cir.getReturnValue() == null){
            if(smarterContraptionStorage$canUseForStorage) {
                smarterContraptionStorage$handlerHelper = FluidHandlerHelper.findSuitableHelper(be);
                if (smarterContraptionStorage$handlerHelper != null) {
                    SmartFluidTank tank = smarterContraptionStorage$handlerHelper.createHandler(be);
                    if(tank != null)
                        cir.setReturnValue(tank);
                }
            }else cir.setReturnValue(new SmartFluidTank(0,null){
                @Override
                public int fill(FluidStack resource, FluidAction action) {
                    return 0;
                }

                @Override
                public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
                    return FluidStack.EMPTY;
                }

                @Override
                public boolean isFluidValid(FluidStack stack) {
                    return false;
                }

                @Override
                public boolean isEmpty() {
                    return true;
                }
            });
        }
    }
    @Inject(method = "removeStorageFromWorld",at = @At("RETURN"),remap = false)
    public void removeStorageFromWorld(CallbackInfo ci){
        if(!valid && smarterContraptionStorage$handlerHelper != null){
            valid = true;
            sendPacket = smarterContraptionStorage$handlerHelper.sendPacket();
            if(tank instanceof NeedDealWith)
                ((NeedDealWith) tank).doSomething(blockEntity);
        }
    }
    @Inject(method = "addStorageToWorld",at = @At("HEAD"),remap = false,cancellable = true)
    public void addStorageToWorld(BlockEntity be, CallbackInfo ci){
        if(smarterContraptionStorage$handlerHelper != null) {
            smarterContraptionStorage$handlerHelper.addStorageToWorld(be,tank);
            ci.cancel();
        }
    }
    @Inject(method = "tick",at = @At("RETURN"),remap = false)
    public void tick(Entity entity, BlockPos pos, boolean isRemote, CallbackInfo ci){
        if(smarterContraptionStorage$handlerHelper != null)
            smarterContraptionStorage$handlerHelper.tick(entity,pos,isRemote);
    }
}
