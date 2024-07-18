package net.smartercontraptionstorage.Mixin.Storage;

import com.simibubi.create.content.contraptions.MountedStorage;
import net.minecraft.world.entity.player.Player;
import net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper;
import net.smartercontraptionstorage.ForFunctionChanger;
import net.smartercontraptionstorage.FunctionChanger;
import net.smartercontraptionstorage.Utils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MountedStorage.class)
public abstract class MountedStorageMixin {
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
            handler = new ItemStackHandler(){
                @Override
                public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                    return stack;
                }
                @Override
                public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                    return ItemStack.EMPTY;
                }
                @Override
                public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                    return false;
                }
            };
            valid = true;
            ci.cancel();
            return;
        }
        smarterContraptionStorage$helper = StorageHandlerHelper.findSuitableHelper(blockEntity);
        if(smarterContraptionStorage$helper != null){
            handler = smarterContraptionStorage$helper.createHandler(blockEntity);
            valid = true;
            ci.cancel();
        }
    }
    @Inject(method = "addStorageToWorld",at = @At("HEAD"),cancellable = true,remap = false)
    public void addStorageToWorld(BlockEntity be, CallbackInfo ci){
        if(smarterContraptionStorage$helper != null) {
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
}