package net.morestorageforcreate.Mixin.Contraption;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.capabilities.DrawerItemHandler;
import com.simibubi.create.content.contraptions.MountedStorage;
import com.simibubi.create.content.logistics.crate.BottomlessItemHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Deprecated
@Mixin(MountedStorage.class)
public class MountedStorageMixin {
    @Shadow(remap = false) @Final private static ItemStackHandler dummyHandler;

    @Shadow(remap = false) private BlockEntity blockEntity;

    @Shadow(remap = false) private boolean valid;

    @Shadow(remap = false) private ItemStackHandler handler;

    @Inject(method = "canUseModdedInventory",at = @At("HEAD"),remap = false,cancellable = true)
    private static void canUseModdedInventory(BlockEntity be, IItemHandler handler, CallbackInfoReturnable<Boolean> cir){
        if(be.getBlockState().getBlock() instanceof BlockDrawers) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
    @Inject(method = "removeStorageFromWorld",at = @At("HEAD"),remap = false, cancellable = true)
    public void removeStorageFromWorld(CallbackInfo ci){
        this.valid = false;
        if(blockEntity == null){
            ci.cancel();
            return;
        }
        IItemHandler beHandler = this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(dummyHandler);
        if(beHandler instanceof DrawerItemHandler){
            handler = new ItemStackHandler(beHandler.getSlots());
            valid = true;
        }
    }
    @Inject(method = "addStorageToWorld",at = @At("HEAD"),remap = false,cancellable = true)
    public void addStorageToWorld(BlockEntity be, CallbackInfo ci){
        if (this.handler instanceof BottomlessItemHandler)
            ci.cancel();
        if(be instanceof BlockEntityDrawers){
            NonNullList<ItemStack> items = NonNullList.withSize(this.handler.getSlots(), ItemStack.EMPTY);

            for(int i = 0; i < items.size(); ++i) {
                items.set(i, this.handler.getStackInSlot(i));
            }
            ci.cancel();
        }
    }
}
