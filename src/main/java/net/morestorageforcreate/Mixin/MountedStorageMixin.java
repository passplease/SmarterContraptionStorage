package net.morestorageforcreate.Mixin;

import com.simibubi.create.content.contraptions.MountedStorage;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.simibubi.create.content.contraptions.MountedStorage.canUseModdedInventory;

@Mixin(MountedStorage.class)
public abstract class MountedStorageMixin{
    @Inject(method = "canUseAsStorage",at = @At("HEAD"),remap = false)
    private static void canUseAsStorage(BlockEntity be, CallbackInfoReturnable<Boolean> cir){
        if (be instanceof ChestBlockEntity) {
            returnStatus(cir,false);
        }
    }
    private static <T> void returnStatus(CallbackInfoReturnable<T> cir,T status){
        cir.setReturnValue(status);
        cir.cancel();
    }
}