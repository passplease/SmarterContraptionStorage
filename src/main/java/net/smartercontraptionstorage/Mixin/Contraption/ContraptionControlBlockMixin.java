package net.smartercontraptionstorage.Mixin.Contraption;

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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.smartercontraptionstorage.MathMethod;
import net.smartercontraptionstorage.SmarterContraptionStorageConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ContraptionControlsBlock.class)
public abstract class ContraptionControlBlockMixin extends ControlsBlock implements IBE<ContraptionControlsBlockEntity>{
    public ContraptionControlBlockMixin(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }
    /**
     * @author passplease
     * @reason <code>@Inject</code> can not work properly. I can use it in IDEA , but it can not be loaded in HMCL,
     * it says can't find "use" method. And because I don't know very well about Mixin , I can't solve it for Excludes.a moment.
     * Thank you for any advice.
     */
    @Overwrite
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit){
        return this.onBlockEntityUse(level, pos, (cte) -> {
            cte.pressButton();
            if (!level.isClientSide()) {
                ContraptionControlsBlockEntity entity = getBlockEntity(level,pos);
                if(entity == null)
                    return InteractionResult.FAIL;
                boolean open;
                if(MathMethod.canBeControlledItem(entity.filtering.getFilter().getItem()))
                    open = !SmarterContraptionStorageConfig.getDefaultOpen(entity.disabled);
                else open = !entity.disabled;
                cte.notifyUpdate();
                cte.disabled = !cte.disabled;
                ContraptionControlsBlockEntity.sendStatus(player, cte.filtering.getFilter(), !open);
                AllSoundEvents.CONTROLLER_CLICK.play(cte.getLevel(), null, cte.getBlockPos(), 1.0F, open ? 0.8F : 1.5F);
            }
            return InteractionResult.SUCCESS;
        });
    }
    // My @Inject method, can not work in HMCL, but can work in IDEA.
    // I think I need to fill refmap file, but I do not know how to fill it.

//    @Inject(method = "use",at = @At("HEAD"),cancellable = true)
//    public void use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir){
//        cir.setReturnValue(onBlockEntityUse(level, pos, (cte) -> {
//            cte.pressButton();
//            if (!level.isClientSide()) {
//                ContraptionControlsBlockEntity entity = GetBlockEntity(level,pos);
//                boolean open;
//                if(MathMethod.canBeControlledBlock(entity.filtering.getFilter().getItem()))
//                    open = !MoreContraptionStorageConfig.getDefaultOpen(entity.disabled);
//                else open = !entity.disabled;
//                cte.notifyUpdate();
//                cte.disabled = !cte.disabled;
//                ContraptionControlsBlockEntity.sendStatus(player, cte.filtering.getFilter(), !open);
//                AllSoundEvents.CONTROLLER_CLICK.play(cte.getLevel(), (Player) null, cte.getBlockPos(), 1.0F, open ? 0.8F : 1.5F);
//            }
//            return InteractionResult.SUCCESS;
//        }));
//    }
}