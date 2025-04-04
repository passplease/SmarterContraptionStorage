package net.smartercontraptionstorage.Mixin.Storage;

import com.simibubi.create.content.contraptions.MountedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper;
import net.smartercontraptionstorage.AddStorage.NeedDealWith;
import net.smartercontraptionstorage.FunctionChanger;
import net.smartercontraptionstorage.Interface.Changeable;
import net.smartercontraptionstorage.Interface.Settable;
import net.smartercontraptionstorage.Utils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper.NULL_HANDLER;

@Mixin(MountedStorage.class)
public abstract class MountedStorageMixin implements Changeable {
    @Shadow(remap = false) private BlockEntity blockEntity;

    @Shadow(remap = false) boolean valid;

    @Shadow(remap = false) ItemStackHandler handler;
    @Unique public StorageHandlerHelper smarterContraptionStorage$helper;
    @Inject(method = "canUseModdedInventory",at = @At("RETURN"),cancellable = true,remap = false)
    private static void canUseModdedInventory(BlockEntity be, IItemHandler handler, CallbackInfoReturnable<Boolean> cir){
        if(!cir.getReturnValue())
            cir.setReturnValue(StorageHandlerHelper.canUseModdedInventory(be));
    }
    @Inject(method = "removeStorageFromWorld",at = @At("HEAD"),cancellable = true,remap = false)
    public void removeStorageFromWorld(CallbackInfo ci){
        smarterContraptionStorage$helper = StorageHandlerHelper.findSuitableHelper(blockEntity);
        if(smarterContraptionStorage$helper != null){
            handler = smarterContraptionStorage$helper.createHandler(blockEntity);
            if(handler instanceof NeedDealWith)
                ((NeedDealWith) handler).doSomething(blockEntity);
            else if(smarterContraptionStorage$helper instanceof NeedDealWith)
                ((NeedDealWith) smarterContraptionStorage$helper).doSomething(blockEntity);
            if(handler == null) {
                handler = NULL_HANDLER;
                valid = false;
            }else valid = true;
            ci.cancel();
        }
    }
    @Inject(method = "addStorageToWorld",at = @At("HEAD"),cancellable = true,remap = false)
    public void addStorageToWorld(BlockEntity be, CallbackInfo ci){
        if(smarterContraptionStorage$helper != null && handler != NULL_HANDLER) {
            smarterContraptionStorage$helper.addStorageToWorld(be, handler);
            ci.cancel();
        }
    }
    @Inject(method = "serialize",at = @At(value = "RETURN"),cancellable = true,remap = false)
    public void serialize(CallbackInfoReturnable<CompoundTag> cir){
        if(smarterContraptionStorage$helper != null){
            CompoundTag tag = cir.getReturnValue();
            if(tag == null)
                return;
            tag.putString(StorageHandlerHelper.DESERIALIZE_MARKER,smarterContraptionStorage$helper.getName());
            if(!smarterContraptionStorage$helper.canDeserialize()) {
                smarterContraptionStorage$helper.addStorageToWorld(blockEntity, handler);
                tag.put("BlockEntity", blockEntity.serializeNBT());
            }
            cir.setReturnValue(tag);
        }
    }
    @Inject(method = "deserialize",at = @At("HEAD"),cancellable = true,remap = false)
    private static void deserialize(CompoundTag nbt, CallbackInfoReturnable<MountedStorage> cir){
        if(nbt.contains(StorageHandlerHelper.DESERIALIZE_MARKER)){
            StorageHandlerHelper helper = null;
            try{
                MountedStorage storage = new MountedStorage(null);
                helper = StorageHandlerHelper.findByName(nbt.getString(StorageHandlerHelper.DESERIALIZE_MARKER));
                if(helper.canDeserialize()) {
                    ((Settable) storage).set(helper.deserialize(nbt));
                }else {
                    BlockPos localPos = NbtUtils.readBlockPos(nbt.getCompound("LocalPos"));
                    BlockEntity blockEntity = FunctionChanger.getBlockEntity.apply(localPos);
                    if(helper.canCreateHandler(blockEntity)) {
                        blockEntity.deserializeNBT(nbt.getCompound("BlockEntity"));
                        ((Settable) storage).set(true,blockEntity,helper.createHandler(blockEntity));
                    }
                }
                ((Settable) storage).set(helper,true);
                cir.setReturnValue(storage);
            }catch (Exception e){
                Utils.addError("Illegal state! Unchecked deserialize try!");
                Utils.addWarning((helper == null ? "Unknown handler" : helper.getName()) + "can't deserialize !");
            }
        }
    }
    @Unique
    @Override
    public void set(Object object) {
        if(object instanceof ItemStackHandler handler)
            this.handler = handler;
        else if(object instanceof Boolean valid)
            this.valid = valid;
        else if(object instanceof StorageHandlerHelper helper)
            this.smarterContraptionStorage$helper = helper;
        else if(object instanceof BlockEntity blockEntity)
            this.blockEntity = blockEntity;
    }

    @Override
    public @Nullable Object get(String name) {
        if(Objects.equals(name, "helper"))
            return this.smarterContraptionStorage$helper;
        else if(Objects.equals(name, "blockEntity"))
            return this.blockEntity;
        return null;
    }
}