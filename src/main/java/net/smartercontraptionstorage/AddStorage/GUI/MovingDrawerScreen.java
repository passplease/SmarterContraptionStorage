package net.smartercontraptionstorage.AddStorage.GUI;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.client.renderer.StorageRenderItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class MovingDrawerScreen extends AbstractMovingScreen<MovingDrawerMenu>{
    public static final ResourceLocation BACKGROUND_1 = new ResourceLocation(StorageDrawers.MOD_ID,"textures/gui/drawers_1.png");

    public static final ResourceLocation BACKGROUND_2 = new ResourceLocation(StorageDrawers.MOD_ID,"textures/gui/drawers_2.png");

    public static final ResourceLocation BACKGROUND_4 = new ResourceLocation(StorageDrawers.MOD_ID,"textures/gui/drawers_4.png");

    protected static StorageRenderItem storageItemRender;
    
    public MovingDrawerScreen(MovingDrawerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = 8;
        titleLabelY = 6;
        inventoryLabelX = 8;
        inventoryLabelY = 105;
        if(storageItemRender == null && minecraft != null) 
            storageItemRender = new StorageRenderItem(minecraft.getTextureManager(), minecraft.getItemRenderer().getItemModelShaper().getModelManager(), minecraft.getItemColors());
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        ItemRenderer preRender = setItemRender(storageItemRender);
        menu.activeRenderItem = storageItemRender;
        super.render(poseStack, mouseX, mouseY, partialTicks);
        menu.activeRenderItem = null;
        storageItemRender.overrideStack = ItemStack.EMPTY;
        setItemRender(preRender);
    }

    protected ItemRenderer setItemRender(ItemRenderer renderItem) {
        ItemRenderer prev = this.itemRenderer;
        this.itemRenderer = renderItem;
        return prev;
    }

    @Override
    protected void renderScreen(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {}

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack, mouseX, mouseY);
        drawContent(poseStack,"container.storagedrawers.upgrades",8f,75f);
    }

    @Override
    public void drawContent(PoseStack poseStack, String key, float x, float y, Object... objects) {
        font.draw(poseStack, I18n.get(key,objects), x, y, 4210752);
    }

    @Override
    public ResourceLocation getBackground() {
        return switch (menu.getHandlerSlot()) {
            case 1 -> BACKGROUND_1;
            case 2 -> BACKGROUND_2;
            case 4 -> BACKGROUND_4;
            default -> (ResourceLocation) menu.throwSlotCountError();
        };
    }

    @Override
    public int height() {
        return 199;
    }

    @Override
    public int width() {
        return 176;
    }

    @Override
    public float getTextureHeight() {
        return 199f / 256f;
    }

    @Override
    public float getTextureWidth() {
        return 176f / 256f;
    }
}
