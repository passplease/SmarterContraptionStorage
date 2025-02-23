package net.smartercontraptionstorage.AddStorage.GUI;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.client.gui.DrawerInfoGuiAddon;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.smartercontraptionstorage.Utils;
import org.apache.commons.lang3.tuple.Pair;

public class MovingFunctionalCompactingScreen extends AbstractMovingScreen<MovingFunctionalCompactingMenu>{
    public static final ResourceLocation TEXTURE = new ResourceLocation(FunctionalStorage.MOD_ID, "textures/blocks/compacting_drawer_front.png");
    public DrawerInfoGuiAddon inventorySlot;
    public MovingFunctionalCompactingScreen(MovingFunctionalCompactingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        setTitleCenter(titleLabelY);
        inventoryLabelY = 91;
        inventorySlot = new DrawerInfoGuiAddon(64,16,TEXTURE, menu.getHandlerSlot(), (slot) -> {
            switch (menu.getHandlerSlot()) {
                case 2 -> {
                    return slot == 0 ? Pair.of(16,28) : Pair.of(16,4);
                }
                case 3 -> {
                    return switch (slot){
                        case 0 -> Pair.of(28,28);
                        case 1 -> Pair.of(4,28);
                        case 2 -> Pair.of(16,4);
                        default -> throw new IllegalStateException("Unexpected slot " + slot + " for " + menu.getHandlerSlot());
                    };
                }
                default -> {
                    menu.throwSlotCountError();
                    Utils.addError("Invalid slot count ! This method should never be called !");
                    return Pair.of(-1,-1);
                }
            }
        },menu.getHandler()::getStackInSlot,(slot) -> menu.getHandler().slotLimits[slot]);
    }

    @Override
    protected void renderScreen(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        if (inventorySlot != null) {
            inventorySlot.drawBackgroundLayer(poseStack,this,null,getGuiLeft(),getGuiTop(),mouseX,mouseY,partialTicks);
        }
        bindTexture(MovingFunctionalDrawerScreen.BACKGROUND);
        for (int slot = 0; slot < 3; slot++) {
            blit(8 + slot * 18,68,0f,height(),20,20,256,256,poseStack);
        }
        for (int slot = 0; slot < 3; slot++) {
            blit(112 + slot * 18,68,0f,height(),20,20,256,256,poseStack);
        }
    }

    @Override
    public ResourceLocation getBackground() {
        return MovingFunctionalDrawerScreen.BACKGROUND;
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack, mouseX, mouseY);
        if(inventorySlot != null) {
            inventorySlot.drawForegroundLayer(poseStack,this,null,getGuiLeft(),getGuiTop(),mouseX,mouseY,this.minecraft.getDeltaFrameTime());
        }
        drawContent(poseStack,"key.categories.storage",10,59);
        drawContent(poseStack,"key.categories.utility",114,59);
    }

    @Override
    public int height() {
        return 184;
    }

    @Override
    public int width() {
        return 176;
    }

    @Override
    public float getTextureHeight() {
        return 184f / 256f;
    }

    @Override
    public float getTextureWidth() {
        return 176f / 256f;
    }
}
