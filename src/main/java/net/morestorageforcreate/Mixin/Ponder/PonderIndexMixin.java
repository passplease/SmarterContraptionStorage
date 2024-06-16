package net.morestorageforcreate.Mixin.Ponder;

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.infrastructure.ponder.PonderIndex;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PonderIndex.class)
public abstract class PonderIndexMixin {
    @Shadow(remap = false) @Final static PonderRegistrationHelper HELPER;

    @Inject(method = "register",at = @At("RETURN"),remap = false)
    private static void register(CallbackInfo ci){
        //HELPER.forComponents().addStoryBoard("contraption_controls", MovementActorScenesMixin::controlStorageBlock);
    }
}