package net.smartercontraptionstorage.AddStorage.GUI;

import com.mojang.blaze3d.vertex.*;
import com.supermartijn642.trashcans.screen.WhitelistButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MovingTrashCanScreen extends AbstractMovingScreen<MovingTrashCanMenu> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation("trashcans","textures/item_screen.png");

    public static final int BUTTON_X_IN_SCREEN = 175;

    public static final int BUTTON_Y_IN_SCREEN = MovingTrashCanMenu.height - 118;

    public WhitelistButton BUTTON;

    public MovingTrashCanScreen(MovingTrashCanMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        setTitleCenter(titleLabelY);
        inventoryLabelX = 21;
        inventoryLabelY = 86;
        BUTTON = new WhitelistButton(BUTTON_X_IN_SCREEN + leftPos,BUTTON_Y_IN_SCREEN + topPos,this::buttonClicked);
        BUTTON.update(menu.getToolboxNumber() == 0 && menu.whiteOrBlack());
        BUTTON.setActive(true);
    }

    @Override
    protected void renderScreen(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        BUTTON.setFocused(checkButtonFocused(mouseX,mouseY));
        BUTTON.render(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack, mouseX, mouseY);
        if(menu.getToolboxNumber() == 0) {
            MutableComponent text = Component.translatable("trashcans.gui.liquid_trash_can.filter");
            if(!menu.getHandler().toolboxItem.isEmpty())
                text.append(Component.translatable("smartercontraptionstorage.moving_container.trashcans.scrolling"));
            font.draw(poseStack, text, 8f, 52f, 4210752);
        } else drawContent(poseStack,"smartercontraptionstorage.moving_container.trashcans.toolbox",8f,52f,menu.getToolboxNumber());
    }

    protected boolean checkButtonFocused(double mouseX, double mouseY) {
        return mouseX >= BUTTON.left() && mouseX <= BUTTON.left() + BUTTON.width() && mouseY >= BUTTON.top() && mouseY <= BUTTON.top() + BUTTON.height();
    }

    @Override
    public ResourceLocation getBackground() {
        return BACKGROUND;
    }

    @Override
    public int height() {
        return MovingTrashCanMenu.height;
    }

    @Override
    public int width() {
        return MovingTrashCanMenu.width;
    }

    public void buttonClicked(){
        if(this.menu.clickMenuButton(getPlayer(),0))
            handleButtonClick(0);
        BUTTON.update(menu.whiteOrBlack());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(menu.getToolboxNumber() == 0 && checkButtonFocused(mouseX, mouseY)) {
            BUTTON.mousePressed((int) mouseX, (int) mouseY, button, false);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if(!super.mouseScrolled(mouseX, mouseY, delta)){
            if(delta < 0.0 && menu.clickMenuButton(getPlayer(),1)) {
                handleButtonClick(1);
                BUTTON.update(false);
            } else if(menu.clickMenuButton(getPlayer(),-1)) {
                handleButtonClick(-1);
                if(menu.getToolboxNumber() == 0)
                    BUTTON.update(menu.whiteOrBlack());
                else BUTTON.update(false);
            }
        }
        return true;
    }
}