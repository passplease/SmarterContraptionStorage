package net.smartercontraptionstorage.Mixin.Contraption;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlock;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.smartercontraptionstorage.Render.Overlay;
import net.smartercontraptionstorage.Utils;
import net.smartercontraptionstorage.SmarterContraptionStorageConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ContraptionControlsBlock.class)
public abstract class ContraptionControlBlockMixin extends ControlsBlock implements IBE<ContraptionControlsBlockEntity> {
    public ContraptionControlBlockMixin(Properties pProperties) {
        super(pProperties);
    }

    @Inject(method = "lambda$useWithoutItem$0",at = @At("HEAD"),remap = false,cancellable = true)
    private static void useWithoutItem_1(Level level, Player player, ContraptionControlsBlockEntity cte, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getMainHandItem();
        if(stack.getItem() instanceof DyeItem item){
            Overlay overlay = ((ContraptionControlsBlockEntityMixin)cte).getOverlay();
            if(overlay == null || !overlay.getColor().equals(item.getDyeColor())) {
                ((ContraptionControlsBlockEntityMixin) cte).setOverlay(Overlay.get(item));
                if (!player.isCreative())
                    stack.shrink(1);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }else if(stack.getItem() == Items.WATER_BUCKET) {
            ((ContraptionControlsBlockEntityMixin) cte).setOverlay(null);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

    @Inject(method = "lambda$useWithoutItem$0",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/actors/contraptionControls/ContraptionControlsBlockEntity;sendStatus(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Z)V"),remap = false,cancellable = true)
    private static void useWithoutItem_2(Level level, Player player, ContraptionControlsBlockEntity cte, CallbackInfoReturnable<InteractionResult> cir) {
        boolean open;
        if(Utils.canBeControlledItem(cte.filtering.getFilter().getItem()))
            open = !SmarterContraptionStorageConfig.getDefaultOpen(!cte.disabled);
        else open = cte.disabled;
        ContraptionControlsBlockEntity.sendStatus(player, cte.filtering.getFilter(), !open);
        AllSoundEvents.CONTROLLER_CLICK.play(cte.getLevel(), null, cte.getBlockPos(), 1.0F, open ? 0.8F : 1.5F);
        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}