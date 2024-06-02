package net.morestorageforcreate.Mixin;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.vault.ItemVaultItem;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Mixin(FilteringBehaviour.class)
public abstract class FilterBehaviourMixin extends BlockEntityBehaviour implements ValueSettingsBehaviour {
    @Shadow(remap = false) private Predicate<ItemStack> predicate;

    public FilterBehaviourMixin(SmartBlockEntity be) {
        super(be);
    }
    @Inject(method = "setFilter(Lnet/minecraft/world/item/ItemStack;)Z",at = @At("HEAD"),remap = false)
    public void setFilterMixin(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        Item filter = stack.copy().getItem();
        if (filter instanceof BlockItem) {
            if (filter instanceof ItemVaultItem || Block.byItem(filter) instanceof ChestBlock)
                this.predicate = (itemStack)-> true;
        }
    }
}
