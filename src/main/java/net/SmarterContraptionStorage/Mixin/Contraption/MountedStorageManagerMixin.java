package net.SmarterContraptionStorage.Mixin.Contraption;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.SmarterContraptionStorage.MathMethod.playerInteracting;

@Mixin(MountedStorageManager.class)
public abstract class MountedStorageManagerMixin {
    @Inject(method = "handlePlayerStorageInteraction",at = @At("HEAD"),remap = false)
    public void head(Contraption contraption, Player player, BlockPos localPos, CallbackInfoReturnable<Boolean> cir){
        playerInteracting = true;
    }
    @Inject(method = "handlePlayerStorageInteraction",at = @At("RETURN"),remap = false)
    public void bottom(Contraption contraption, Player player, BlockPos localPos, CallbackInfoReturnable<Boolean> cir){
        playerInteracting = false;
    }
}
