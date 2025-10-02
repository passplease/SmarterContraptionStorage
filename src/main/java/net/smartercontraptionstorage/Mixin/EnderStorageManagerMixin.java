package net.smartercontraptionstorage.Mixin;

import codechicken.enderstorage.manager.EnderStorageManager;
import net.smartercontraptionstorage.AddStorage.FluidHander.EnderTankHelper;
import net.smartercontraptionstorage.AddStorage.ItemHandler.EnderChestHandlerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderStorageManager.class)
public class EnderStorageManagerMixin {
    @Inject(method = "reloadManager",at = @At("RETURN"),remap = false)
    private static void reloadManager(boolean client, CallbackInfo ci){
        EnderChestHandlerHelper.EnderChestHandler.HandlerReload++;
        EnderTankHelper.EnderStorageManagerWrap.HandlerReload++;
    }
}
