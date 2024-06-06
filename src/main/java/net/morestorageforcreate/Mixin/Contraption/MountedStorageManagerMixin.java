package net.morestorageforcreate.Mixin.Contraption;

import com.simibubi.create.content.contraptions.MountedStorageManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.morestorageforcreate.MathMethod.moreStorageForCreate$skipAdd;

@Mixin(MountedStorageManager.class)
public abstract class MountedStorageManagerMixin {
    @Inject(method = "addBlock",at = @At("HEAD"),remap = false,cancellable = true)
    public void addBlock(BlockPos localPos, BlockEntity be, CallbackInfo ci){
        if(moreStorageForCreate$skipAdd)
            ci.cancel();
        moreStorageForCreate$skipAdd = false;
    }
}
