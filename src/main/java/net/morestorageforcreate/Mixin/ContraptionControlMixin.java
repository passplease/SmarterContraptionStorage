package net.morestorageforcreate.Mixin;

import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.annotation.Target;

@Mixin(ContraptionControlsBlockEntity.class)
public abstract class ContraptionControlMixin extends SmartBlockEntity {
    public ContraptionControlMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    @Inject(method = "pressButton()V",at = @At("HEAD"),remap = false)
    public void pressButton(CallbackInfo ci){
        System.out.println(11111);
    }
}