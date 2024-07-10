package net.smartercontraptionstorage.Mixin;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.world.item.ItemStack;
import net.smartercontraptionstorage.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(FilteringBehaviour.class)
public abstract class FilterBehaviourMixin extends BlockEntityBehaviour implements ValueSettingsBehaviour {

    @Shadow(remap = false) private Predicate<ItemStack> predicate;

    public FilterBehaviourMixin(SmartBlockEntity be) {
        super(be);
    }
    @Inject(method = "setFilter(Lnet/minecraft/world/item/ItemStack;)Z",at = @At("HEAD"),remap = false)
    public void setFilterMixin(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        if (Utils.canBeControlledItem(stack.getItem()))
            this.predicate = (itemStack)-> true;
    }
}
