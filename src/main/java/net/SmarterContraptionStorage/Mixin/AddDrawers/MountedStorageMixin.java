package net.SmarterContraptionStorage.Mixin.AddDrawers;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.capabilities.DrawerItemHandler;
import com.simibubi.create.content.contraptions.MountedStorage;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Inject(method = "canUseModdedInventory",at = @At("HEAD"),cancellable = true,remap = false)
    private static void canUseModdedInventory(BlockEntity be, IItemHandler handler, CallbackInfoReturnable<Boolean> cir){
        if(handler instanceof DrawerItemHandler){
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
    @Inject(method = "removeStorageFromWorld",at = @At("HEAD"),cancellable = true,remap = false)
    public void removeStorageFromWorld(CallbackInfo ci){
        if(blockEntity instanceof BlockEntityDrawers){
            handler = new ItemStackHandler(){
                int[] limits;
                @Override
                public void setSize(int size) {
                    super.setSize(size);
                    limits = new int[size];
                }
                @Override
                public int getSlotLimit(int slot) {
                    return limits[slot];
                }
                @Override
                public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                    super.setStackInSlot(slot, stack);
                    limits[slot] = stack.getMaxStackSize();
                }
            };
            IDrawerGroup group = ((BlockEntityDrawers) blockEntity).getGroup();
            handler.setSize(group.getDrawerCount());
            for(int i = handler.getSlots() - 1; i >= 0;i--){
                handler.setStackInSlot(i,new ItemStack(group.getDrawer(i).getStoredItemPrototype().getItem(),group.getDrawer(i).getStoredItemCount()));
                group.getDrawer(i).setStoredItemCount(0);
            }
            valid = true;
            ci.cancel();
        }
    }
    @Inject(method = "addStorageToWorld",at = @At("HEAD"),cancellable = true,remap = false)
    public void addStorageToWorld(BlockEntity be, CallbackInfo ci){
        if(be instanceof BlockEntityDrawers){
            IDrawerGroup group = ((BlockEntityDrawers) be).getGroup();
            for(int i = handler.getSlots() - 1;i >= 0;i--){
                group.getDrawer(i).setStoredItem(handler.getStackInSlot(i),handler.getStackInSlot(i).getCount());
            }
                ci.cancel();
        }
    }
}
