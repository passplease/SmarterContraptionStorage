package net.smartercontraptionstorage.AddStorage.GUI;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MovingFCompactingDrawerScreen extends AbstractMovingScreen<MovingFCompactingDrawerMenu>{
    public MovingFCompactingDrawerScreen(MovingFCompactingDrawerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void renderScreen(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {

    }

    @Override
    public ResourceLocation getBackground() {
        return null;
    }

    @Override
    public int height() {
        return 0;
    }

    @Override
    public int width() {
        return 0;
    }
}
