package net.morestorageforcreate.Mixin;

import com.simibubi.create.compat.jei.category.BlockCuttingCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockCuttingCategory.CondensedBlockCuttingRecipe.class)
public abstract class StoneCutterMixin extends StonecutterRecipe {
    @Shadow public abstract void addOutput(ItemStack stack);

    public StoneCutterMixin(ResourceLocation pId, String pGroup, Ingredient pIngredient, ItemStack pResult) {
        super(pId, pGroup, pIngredient, pResult);
    }
    @Inject(method = "<init>",at = @At("TAIL"),remap = false)
    private void renameItem(Ingredient ingredient, CallbackInfo ci){
        ItemStack[] items = ingredient.getItems();
        for (int i = 0;i < items.length;i++){
            ItemStack item = items[i];
        }
    }
}
