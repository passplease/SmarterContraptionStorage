package net.morestorageforcreate.Mixin.Contraption;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlock;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.morestorageforcreate.MathMethod;
import net.morestorageforcreate.MoreContraptionStorageConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ContraptionControlsBlock.class)
public abstract class ContraptionControlBlockMixin extends ControlsBlock implements IBE<ContraptionControlsBlockEntity> {
    public ContraptionControlBlockMixin(Properties p_54120_) {
        super(p_54120_);
    }
    @Inject(method = "use",at = @At("HEAD"),cancellable = true,remap = false)
    public void changeResult(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir){
        cir.setReturnValue(this.onBlockEntityUse(level, pos, (cte) -> {
            cte.pressButton();
            if (!level.isClientSide()) {
                ContraptionControlsBlockEntity entity = getBlockEntity(level,pos);
                boolean open;
                if(MathMethod.canBeControlledBlock(entity.filtering.getFilter().getItem()))
                    open = !MoreContraptionStorageConfig.getDefaultOpen(entity.disabled);
                else open = !entity.disabled;
                cte.notifyUpdate();
                cte.disabled = !cte.disabled;
                ContraptionControlsBlockEntity.sendStatus(player, cte.filtering.getFilter(), !open);
                AllSoundEvents.CONTROLLER_CLICK.play(cte.getLevel(), (Player) null, cte.getBlockPos(), 1.0F, open ? 0.8F : 1.5F);
            }
            return InteractionResult.SUCCESS;
        }));
        cir.cancel();
    }
}