package net.smartercontraptionstorage.Mixin.Storage;

import com.simibubi.create.content.contraptions.MountedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.world.entity.player.Player;
import net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper;
import net.smartercontraptionstorage.AddStorage.NeedDealWith;
import net.smartercontraptionstorage.ForFunctionChanger;
import net.smartercontraptionstorage.FunctionChanger;
import net.smartercontraptionstorage.Settable;
import net.smartercontraptionstorage.Utils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper.NULL_HANDLER;

@Mixin(MountedStorage.class)
public abstract class MountedStorageMixin implements Settable {
    @Shadow(remap = false) private BlockEntity blockEntity;

    @Shadow(remap = false) boolean valid;

    @Shadow(remap = false) @Final private static ItemStackHandler dummyHandler;

    @Shadow(remap = false) ItemStackHandler handler;
    @Unique public StorageHandlerHelper smarterContraptionStorage$helper;
    @Unique public boolean smarterContraptionStorage$canUseForStorage;
    @Inject(method = "canUseModdedInventory",at = @At("RETURN"),cancellable = true,remap = false)
    private static void canUseModdedInventory(BlockEntity be, IItemHandler handler, CallbackInfoReturnable<Boolean> cir){
        if(!cir.getReturnValue())
            cir.setReturnValue(StorageHandlerHelper.canUseModdedInventory(be));
    }
    @Inject(method = "removeStorageFromWorld",at = @At("HEAD"),cancellable = true,remap = false)
    public void removeStorageFromWorld(CallbackInfo ci){
        if(!smarterContraptionStorage$canUseForStorage){
            handler = NULL_HANDLER;
            valid = true;
            ci.cancel();
            return;
        }
        smarterContraptionStorage$helper = StorageHandlerHelper.findSuitableHelper(blockEntity);
        if(smarterContraptionStorage$helper != null){
            handler = smarterContraptionStorage$helper.createHandler(blockEntity);
            if(handler instanceof NeedDealWith)
                ((NeedDealWith) handler).doSomething(blockEntity);
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
    @Inject(method = "<init>",at = @At("RETURN"))
    public void init(BlockEntity be, CallbackInfo ci){
        smarterContraptionStorage$canUseForStorage = Utils.smarterContraptionStorage$canUseForStorage;
        Utils.smarterContraptionStorage$canUseForStorage = true;
    }
    /**
     * @author PassPlease
     * @reason
     * To make sure we can open closed containers
     */
    @Overwrite(remap = false)
    public IItemHandlerModifiable getItemHandler(){
        if(Utils.playerInteracting && !smarterContraptionStorage$canUseForStorage) {
            MountedStorage storage = new MountedStorage(blockEntity);
            storage.removeStorageFromWorld();
            // To generate storage.handler, though, it makes much redundancy calculation
            return storage.getItemHandler();
        }
        return handler;
    }
    @Inject(method = "serialize",at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemStackHandler;serializeNBT()Lnet/minecraft/nbt/CompoundTag;"),remap = false)
    public void serialize(CallbackInfoReturnable<CompoundTag> cir){
        if(smarterContraptionStorage$helper != null && !smarterContraptionStorage$helper.canDeserialize()){
            smarterContraptionStorage$helper.addStorageToWorld(blockEntity,handler);
            CompoundTag tag = new CompoundTag();
            tag.put("pos",NbtUtils.writeBlockPos(blockEntity.getBlockPos()));
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
                    BlockPos blockPos = NbtUtils.readBlockPos(nbt.getCompound("pos"));
                    BlockEntity blockEntity = FunctionChanger.presentBlockEntities.get(blockPos);
                    if(helper.canCreateHandler(blockEntity)) {
                        ((Settable) storage).set(helper.canCreateHandler(blockEntity));
                        ((Settable) storage).set(blockEntity);
                    }
                }
                ((Settable) storage).set(helper);
                ((Settable) storage).set(true);
                cir.setReturnValue(storage);
                cir.cancel();
            }catch (Exception e){
                Utils.addError("Illegal state! Unchecked deserialize try!");
                Utils.addWarning((helper == null ? "Unknown handler" : helper.getName()) + "can't deserialize !");
            }
        }
    }
    @ForFunctionChanger(method = "findMountedEntity")
    @Inject(method = "isValid",at = @At("HEAD"),remap = false)
    public void getBlockEntity(CallbackInfoReturnable<Boolean> cir){
        if(FunctionChanger.isMountedEntity())
            FunctionChanger.setMounted_entity(this.blockEntity);
    }
    @Deprecated
    @ForFunctionChanger(method = "openGUI")
    @Inject(method = "canUseForFuel",at = @At("HEAD"),cancellable = true,remap = false)
    public void openGUI(CallbackInfoReturnable<Boolean> cir){
        if(FunctionChanger.isOpenGUI()){
            if(smarterContraptionStorage$helper != null && smarterContraptionStorage$helper.canHandlerCreateMenu()) {
                Player player = FunctionChanger.getPlayer();
                player.containerMenu = player.inventoryMenu;
                player.openMenu(smarterContraptionStorage$helper.createHandlerMenu(blockEntity,handler,player));
                cir.setReturnValue(true);
            }else
                cir.setReturnValue(false);
            cir.cancel();
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
}