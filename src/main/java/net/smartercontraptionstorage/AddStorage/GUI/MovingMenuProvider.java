package net.smartercontraptionstorage.AddStorage.GUI;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.smartercontraptionstorage.SmarterContraptionStorage;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MovingMenuProvider extends MenuProvider{
    default @NotNull Component getDisplayName() {
        return Component.translatable(SmarterContraptionStorage.MODID + ".moving_container." + getTranslationKey());
    }

    String getTranslationKey();

    void setContraption(AbstractContraptionEntity contraption);

    AbstractContraptionEntity getContraption();

    void setLocalPos(BlockPos localPos);

    BlockPos getLocalPos();

    default boolean stillValid(Player player){
        return getContraption().isAlive() && player.distanceToSqr(getContraption().toGlobalVector(Vec3.atCenterOf(getLocalPos()), 0.0F)) < (double)64.0F;
    }

    MenuType<? extends AbstractMovingMenu<?>> getMenuType();

    @Nullable AbstractMovingMenu<?> createMenu(int id, Inventory inventory, Player player);

    default boolean check(){
        return getContraption() != null && getContraption().isAlive() && getLocalPos() != null;
    }

    default void error(){
        setContraption(null);
        setLocalPos(null);
        Utils.addError(getTranslationKey() + " can not open menu !");
    }

    void writeToBuffer(@NotNull FriendlyByteBuf buffer);

    default void playSound(Level level){
        level.playSound(null, new BlockPos(getSoundPos()), SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 0.75f, 1.0f);
    }

    default Vec3 getSoundPos(){
        return getContraption().toGlobalVector(Vec3.atCenterOf(getLocalPos()), 0.0f);
    }

    default void removed(AbstractMovingScreen<?> screen,Player player){}
}