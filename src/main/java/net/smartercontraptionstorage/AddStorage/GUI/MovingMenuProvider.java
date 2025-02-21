package net.smartercontraptionstorage.AddStorage.ItemHandler;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.smartercontraptionstorage.SmarterContraptionStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface MovingMenuProvider extends MenuProvider{
    default @NotNull Component getDisplayName() {
        return Component.translatable(SmarterContraptionStorage.MODID + "moving_container." + getTranslationKey());
    }

    String getTranslationKey();

    void setValid(Supplier<Boolean> valid);

    boolean stillValid(Player player);

    MenuType<? extends AbstractMovingMenu<?>> getMenuType();

    @Nullable AbstractMovingMenu<?> createMenu(int id, Inventory inventory, Player player);
}