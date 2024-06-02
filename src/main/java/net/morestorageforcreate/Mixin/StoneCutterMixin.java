package net.morestorageforcreate.Mixin;

import com.simibubi.create.compat.jei.category.BlockCuttingCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(BlockCuttingCategory.CondensedBlockCuttingRecipe.class)
public abstract class StoneCutterMixin extends StonecutterRecipe {
    @Shadow(remap = false) private List<ItemStack> outputs;

    public StoneCutterMixin(ResourceLocation pId, String pGroup, Ingredient pIngredient, ItemStack pResult) {
        super(pId, pGroup, pIngredient, pResult);
    }
    private static final Pattern NUMBERNAME = Pattern.compile("[0-9]*");
    @Inject(method = "<init>",at = @At("TAIL"),remap = false)
    private void renameItem(Ingredient ingredient, CallbackInfo ci){
        ItemStack[] items = ingredient.getItems();
        for (int i = 0;i < items.length;i++){
            ItemStack item = items[i];
            String name = item.getDisplayName().getString();
            if(!NUMBERNAME.matcher(name).matches())
                name = String.valueOf(1);
            else
                name = String.valueOf(Integer.parseInt(name) + 1);
            item.setHoverName(Component.literal(name));
            outputs.add(item);
        }
    }
}
