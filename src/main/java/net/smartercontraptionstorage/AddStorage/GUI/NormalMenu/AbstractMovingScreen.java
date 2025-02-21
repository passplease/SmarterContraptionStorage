package net.smartercontraptionstorage.AddStorage.GUI.NormalMenu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractMovingScreen<T extends AbstractMovingMenu<?>> extends AbstractContainerScreen<T>{
    public AbstractMovingScreen(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        imageWidth = width();
        imageHeight = height();
        super.init();
    }

    @Override
    protected void renderBg(@NotNull PoseStack poseStack, float v, int mouseX, int mouseY) {
        renderBackground(poseStack);
        poseStack.pushPose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexture(getBackground(),poseStack,leftPos,topPos,width() + leftPos,height() + topPos,getTextureLeft(),getTextureTop(),getTextureLeft() + getTextureWidth(),getTextureTop() + getTextureHeight());
        renderScreen(poseStack, v, mouseX, mouseY);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
        renderTooltip(poseStack, mouseX, mouseY);
    }

    protected abstract void renderScreen(PoseStack poseStack, float partialTicks, int mouseX, int mouseY);

    public void drawTexture(ResourceLocation texture,PoseStack poseStack, float left, float top, float right, float bottom, float textureLeft, float textureTop, float textureRight, float textureBottom) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture(texture);
        Matrix4f matrix = poseStack.last().pose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, left, bottom, 0.0F).uv(textureLeft, textureBottom).endVertex();
        buffer.vertex(matrix, right, bottom, 0.0F).uv(textureRight, textureBottom).endVertex();
        buffer.vertex(matrix, right, top, 0.0F).uv(textureRight, textureTop).endVertex();
        buffer.vertex(matrix, left, top, 0.0F).uv(textureLeft, textureTop).endVertex();
        tesselator.end();
    }

    public void bindTexture(ResourceLocation location) {
        RenderSystem.setShaderTexture(0, location);
    }

    public void blit(int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight,PoseStack poseStack){
        blit(poseStack, getXofScreen(x), getYofScreen(y), u, v, width, height, textureWidth, textureHeight);
    }

    public abstract ResourceLocation getBackground();

    public void setTitleCenter(int y) {
        titleLabelX = getCenterX() - font.width(getTitle()) / 2;
        titleLabelY = y;
    }

    public void drawContent(PoseStack poseStack,String key,float x,float y,Object... objects) {
        font.draw(poseStack,Component.translatable(key,objects),x,y,4210752);
    }

    public int getCenterX(){
        return width() / 2;
    }

    public abstract int height();

    public abstract int width();

    public float getTextureLeft(){
        return 0f;
    }

    public float getTextureTop(){
        return 0f;
    }

    public float getTextureWidth(){
        return 1f;
    }

    public float getTextureHeight(){
        return 1f;
    }

    public int getXofScreen(int x){
        return x + leftPos;
    }

    public int getYofScreen(int y){
        return y + topPos;
    }

    public float getXofScreen(float x){
        return x + leftPos;
    }

    public float getYofScreen(float y){
        return y + topPos;
    }

    public void handleButtonClick(int id){
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId,id);
    }

    public Player getPlayer(){
        return this.minecraft.player;
    }
}