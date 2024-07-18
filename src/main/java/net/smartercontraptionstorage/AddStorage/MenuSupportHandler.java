package net.smartercontraptionstorage.AddStorage;

import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
@Deprecated
// Cannot open GUI in each inheritor, and I don't know how to solve this.
// But I can open Minecraft's GUI such as CraftingTable and so on (replace the proper GUI it should be).
public interface MenuSupportHandler {
    @Deprecated
    default boolean canHandlerCreateMenu(){
        return false;
    }
    @Deprecated
    @Nullable
    default MenuProvider createHandlerMenu(BlockEntity entity, ItemStackHandler handler, @Nullable Player player){
        return null;
    }
}