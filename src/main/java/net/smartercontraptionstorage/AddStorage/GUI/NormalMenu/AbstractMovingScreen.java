package net.smartercontraptionstorage.AddStorage.GUI.NormalMenu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;

import javax.annotation.Nullable;

public abstract class AbstractMovingScreen<T extends AbstractMovingMenu<?>> extends AbstractContainerScreen<T>{
    @Nullable private ResourceLocation bindTexture;

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
    protected void renderBg(GuiGraphics guiGraphics, float v, int mouseX, int mouseY) {
        renderBackground(guiGraphics);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexture(getBackground(),guiGraphics,leftPos,topPos,width() + leftPos,height() + topPos,getTextureLeft(),getTextureTop(),getTextureLeft() + getTextureWidth(),getTextureTop() + getTextureHeight());
        renderScreen(guiGraphics, v, mouseX, mouseY);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    protected abstract void renderScreen(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY);

    public void drawTexture(ResourceLocation texture,GuiGraphics guiGraphics, float left, float top, float right, float bottom, float textureLeft, float textureTop, float textureRight, float textureBottom) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture(texture);
        Matrix4f matrix = guiGraphics.pose().last().pose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, left, bottom, 0.0F).uv(textureLeft, textureBottom).endVertex();
        buffer.vertex(matrix, right, bottom, 0.0F).uv(textureRight, textureBottom).endVertex();
        buffer.vertex(matrix, right, top, 0.0F).uv(textureRight, textureTop).endVertex();
        buffer.vertex(matrix, left, top, 0.0F).uv(textureLeft, textureTop).endVertex();
        tesselator.end();
    }

    public void bindTexture(@Nullable ResourceLocation location) {
        if (location != null)
            RenderSystem.setShaderTexture(0, location);
        bindTexture = location;
    }

    public void blit(int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight,GuiGraphics guiGraphics){
        if(bindTexture != null)
            guiGraphics.blit(bindTexture,x,y,u,v,width,height,textureWidth,textureHeight);
    }

    public abstract ResourceLocation getBackground();

    public void setTitleCenter(int y) {
        titleLabelX = getCenterX() - font.width(getTitle()) / 2;
        titleLabelY = y;
    }

    public void drawContent(GuiGraphics guiGraphics,String key,float x,float y,Object... objects) {
        guiGraphics.drawString(font,Component.translatable(key,objects).getString(),x,y,4210752,false);
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