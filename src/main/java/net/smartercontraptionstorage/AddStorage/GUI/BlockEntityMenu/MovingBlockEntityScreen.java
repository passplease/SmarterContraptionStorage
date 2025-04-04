package net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.smartercontraptionstorage.SmarterContraptionStorage;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class MovingBlockEntityScreen extends AbstractContainerScreen<MovingBlockEntityMenu> {
    private final AbstractContainerScreen<?> screen;

    public static final String TITLE_KEY = SmarterContraptionStorage.MODID + ".moving_blockentity_container";

    public MovingBlockEntityScreen(MovingBlockEntityMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, generateTitle(title));
        if(menu.getHelper().checkMenu(menu))
            screen = menu.getHelper().createScreen(menu, inventory, this.title);
        else throw new IllegalStateException("Invalid MovingBlockEntityMenu !");
    }

    public void init(Minecraft pMinecraft, int pWidth, int pHeight) {
        getScreen().init(pMinecraft, pWidth, pHeight);
        super.init(pMinecraft, pWidth, pHeight);
    }

    public static Component generateTitle(Component title) {
        if(Minecraft.getInstance().options.languageCode.equals(Language.DEFAULT))
            return Component.translatable(TITLE_KEY).append(title);
        else {
            MutableComponent moving = Component.translatable(TITLE_KEY);
            if (moving.getString().equals("Moving "))
                return title;
            else return moving.append(title);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        getScreen().render(poseStack, mouseX, mouseY, partialTick);
        getMenu().getHelper().render(this,poseStack,mouseX,mouseY,partialTick);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float v, int i, int i1) {
        throw new IllegalCallerException("This method should not be called !");
    }

    public AbstractContainerScreen<?> getScreen() {
        return screen;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return getScreen().keyPressed(pKeyCode,pScanCode,pModifiers);
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        return getScreen().keyReleased(pKeyCode,pScanCode,pModifiers);
    }

    @Override
    public void onClose() {
        super.onClose();
        getScreen().onClose();
    }

    @Override
    public void removed() {
        getScreen().removed();
        super.removed();
    }

    @Override
    public void renderTooltip(PoseStack poseStack, List<? extends FormattedCharSequence> lines, int x, int y, Font font) {
        getScreen().renderTooltip(poseStack, lines, x, y, font);
    }

    @Override
    public void renderTooltip(PoseStack pPoseStack, List<? extends FormattedCharSequence> pTooltips, int pMouseX, int pMouseY) {
        getScreen().renderTooltip(pPoseStack, pTooltips, pMouseX, pMouseY);
    }

    @Override
    public void renderTooltip(PoseStack pPoseStack, Component pText, int pMouseX, int pMouseY) {
        getScreen().renderTooltip(pPoseStack, pText, pMouseX, pMouseY);
    }

    @Override
    public void renderTooltip(PoseStack poseStack, List<Component> textComponents, Optional<TooltipComponent> tooltipComponent, int x, int y, @Nullable Font font) {
        getScreen().renderTooltip(poseStack, textComponents, tooltipComponent, x, y, font);
    }

    @Override
    public void renderTooltip(PoseStack pPoseStack, List<Component> pTooltips, Optional<TooltipComponent> pVisualTooltipComponent, int pMouseX, int pMouseY) {
        getScreen().renderTooltip(pPoseStack,pTooltips,pVisualTooltipComponent,pMouseX,pMouseY);
    }

    @Override
    public void renderTooltip(PoseStack poseStack, List<Component> textComponents, Optional<TooltipComponent> tooltipComponent, int x, int y, ItemStack stack) {
        getScreen().renderTooltip(poseStack,textComponents,tooltipComponent,x,y);
    }

    @Override
    public void renderTooltip(PoseStack poseStack, List<Component> textComponents, Optional<TooltipComponent> tooltipComponent, int x, int y, @Nullable Font font, ItemStack stack) {
        getScreen().renderTooltip(poseStack,textComponents,tooltipComponent,x,y);
    }

    @Override
    public List<Component> getTooltipFromItem(ItemStack pItemStack) {
        return getScreen().getTooltipFromItem(pItemStack);
    }

    @Override
    public void renderBackground(PoseStack pPoseStack) {
        getScreen().renderBackground(pPoseStack);
    }

    @Override
    public void renderBackground(PoseStack pPoseStack, int pVOffset) {
        getScreen().renderBackground(pPoseStack,pVOffset);
    }

    @Override
    public void renderComponentTooltip(PoseStack pPoseStack, List<Component> pTooltips, int pMouseX, int pMouseY) {
        getScreen().renderComponentTooltip(pPoseStack,pTooltips,pMouseX,pMouseY);
    }

    @Override
    public void renderComponentTooltip(PoseStack poseStack, List<? extends FormattedText> tooltips, int mouseX, int mouseY, @Nullable Font font) {
        getScreen().renderComponentTooltip(poseStack,tooltips,mouseX,mouseY,font);
    }

    @Override
    public void renderComponentTooltip(PoseStack poseStack, List<? extends FormattedText> tooltips, int mouseX, int mouseY, ItemStack stack) {
        getScreen().renderComponentTooltip(poseStack,tooltips,mouseX,mouseY,stack);
    }

    @Override
    public void renderComponentTooltip(PoseStack poseStack, List<? extends FormattedText> tooltips, int mouseX, int mouseY, @Nullable Font font, ItemStack stack) {
        getScreen().renderComponentTooltip(poseStack,tooltips,mouseX,mouseY,font,stack);
    }

    @Override
    public void renderDirtBackground(int pVOffset) {
        getScreen().renderDirtBackground(pVOffset);
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        getScreen().resize(pMinecraft,pWidth,pHeight);
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return getScreen().isMouseOver(pMouseX,pMouseY);
    }

    @Override
    public void onFilesDrop(List<Path> pPacks) {
        getScreen().onFilesDrop(pPacks);
    }

    @Override
    public void afterMouseMove() {
        getScreen().afterMouseMove();
    }

    @Override
    public void afterKeyboardAction() {
        getScreen().afterKeyboardAction();
    }

    @Override
    public void afterMouseAction() {
        getScreen().afterMouseAction();
    }

    @Override
    public void handleDelayedNarration() {
        getScreen().handleDelayedNarration();
    }

    @Override
    public void triggerImmediateNarration(boolean p_169408_) {
        getScreen().triggerImmediateNarration(p_169408_);
    }

    @Override
    public void narrationEnabled() {
        getScreen().narrationEnabled();
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return getMenu().getHelper().shouldClickScreen(this, pMouseX, pMouseY, pButton) && getScreen().mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return getScreen().mouseDragged(pMouseX,pMouseY,pButton,pDragX,pDragY);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        return getScreen().mouseReleased(pMouseX,pMouseY,pButton);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        return getScreen().mouseScrolled(pMouseX,pMouseY,pDelta);
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        getScreen().mouseMoved(pMouseX,pMouseY);
    }

    @Override
    public void clearDraggingState() {
        super.clearDraggingState();
        getScreen().clearDraggingState();
    }

    @Override
    public void tick() {
        getScreen().tick();
    }

    @Override
    public void slotClicked(Slot pSlot, int pSlotId, int pMouseButton, ClickType pType) {
        if(getMenu().getHelper().slotClicked(this,pSlot,pSlotId,pMouseButton,pType))
            super.slotClicked(pSlot, pSlotId, pMouseButton, pType);
    }
}
