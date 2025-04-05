package net.smartercontraptionstorage.AddStorage.GUI.NormalMenu;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.smartercontraptionstorage.AddStorage.GUI.ContraptionMenuProvider;
import net.smartercontraptionstorage.SmarterContraptionStorage;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MovingMenuProvider extends ContraptionMenuProvider<AbstractMovingMenu<?>> {
    default @NotNull Component getDisplayName() {
        return Component.translatable(SmarterContraptionStorage.MODID + ".moving_container." + getTranslationKey());
    }

    String getTranslationKey();

    MenuType<? extends AbstractMovingMenu<?>> getMenuType();

    @Override
    default void rememberPlayer(ServerPlayer player){}

    @Override
    default @Nullable ServerPlayer getPlayer(){
        return null;
    }

    @Override
    default boolean hasOpened(){
        return getContraption() != null || getLocalPos() != null;
    }
}