package net.smartercontraptionstorage.Mixin.Storage;

import com.simibubi.create.content.contraptions.MountedFluidStorage;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.smartercontraptionstorage.AddStorage.FluidHander.FluidHandlerHelper;
import net.smartercontraptionstorage.AddStorage.NeedDealWith;
import net.smartercontraptionstorage.FunctionChanger;
import net.smartercontraptionstorage.Interface.Settable;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MountedFluidStorage.class)
public class MountedFluidStorageMixin implements Settable {
    @Shadow(remap = false)
    SmartFluidTank tank;
    @Shadow(remap = false) private boolean valid;
    @Shadow(remap = false) private boolean sendPacket;
    @Shadow(remap = false) private BlockEntity blockEntity;
    @Unique @Nullable
    FluidHandlerHelper smarterContraptionStorage$handlerHelper;
    @Inject(method = "canUseAsStorage",at = @At("RETURN"),remap = false, cancellable = true)
    private static void canUseAsStorage(BlockEntity be, CallbackInfoReturnable<Boolean> cir){
        if(!cir.getReturnValue())
            cir.setReturnValue(FluidHandlerHelper.canUseAsStorage(be));
    }
    @Inject(method = "createMountedTank",at = @At("RETURN"),remap = false, cancellable = true)
    public void createMountedTank(BlockEntity be, CallbackInfoReturnable<SmartFluidTank> cir){
        if(cir.getReturnValue() == null){
            smarterContraptionStorage$handlerHelper = FluidHandlerHelper.findSuitableHelper(be);
            if (smarterContraptionStorage$handlerHelper != null) {
                SmartFluidTank tank = smarterContraptionStorage$handlerHelper.createHandler(be);
                cir.setReturnValue(tank);
            }
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
    @Inject(method = "serialize",at = @At("RETURN"),remap = false,cancellable = true)
    public void serialize(CallbackInfoReturnable<CompoundTag> cir){
        if(smarterContraptionStorage$handlerHelper != null){
            CompoundTag tag = cir.getReturnValue();
            if(tag == null)
                return;
            tag.putString(FluidHandlerHelper.DESERIALIZE_MARKER,smarterContraptionStorage$handlerHelper.getName());
            if(!smarterContraptionStorage$handlerHelper.canDeserialize()){
                smarterContraptionStorage$handlerHelper.addStorageToWorld(blockEntity,tank);
                tag.put("pos", NbtUtils.writeBlockPos(blockEntity.getBlockPos()));
            }
            cir.setReturnValue(tag);
        }
    }
    @Inject(method = "deserialize",at = @At("HEAD"),remap = false,cancellable = true)
    private static void deserialize(CompoundTag nbt, CallbackInfoReturnable<MountedFluidStorage> cir){
        if(nbt.contains(FluidHandlerHelper.DESERIALIZE_MARKER)){
            FluidHandlerHelper helper = null;
            try{
                MountedFluidStorage storage = new MountedFluidStorage(null);
                helper = FluidHandlerHelper.findByName(nbt.getString(FluidHandlerHelper.DESERIALIZE_MARKER));
                if(helper.canDeserialize()) {
                    ((Settable) storage).set(helper.deserialize(nbt));
                } else {
                    BlockPos blockPos = NbtUtils.readBlockPos(nbt.getCompound("pos"));
                    BlockEntity blockEntity = FunctionChanger.presentBlockEntities.get(blockPos);
                    if(helper.canCreateHandler(blockEntity)) {
                        helper.createHandler(blockEntity);
                        ((Settable) storage).set(blockEntity);
                    }
                }
                ((Settable)storage).set(helper,true);
                cir.setReturnValue(storage);
            } catch (Exception e) {
                Utils.addError("Illegal state! Unchecked deserialize try!");
                Utils.addWarning((helper == null ? "Unknown fluid handler" : helper.getName()) + "can't deserialize !");
            }
        }
    }

    @Override
    public void set(Object object) {
        if(object instanceof SmartFluidTank)
            this.tank = (SmartFluidTank) object;
        else if(object instanceof Boolean)
            this.valid = (boolean) object;
        else if(object instanceof FluidHandlerHelper)
            this.smarterContraptionStorage$handlerHelper = (FluidHandlerHelper) object;
        else if(object instanceof BlockEntity)
            this.blockEntity = (BlockEntity) object;
    }
}