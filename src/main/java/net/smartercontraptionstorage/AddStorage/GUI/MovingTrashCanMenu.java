package net.smartercontraptionstorage.AddStorage.GUI;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.SlotItemHandler;
import net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper;
import net.smartercontraptionstorage.AddStorage.ItemHandler.TrashHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class ItemTrashCanMenu extends AbstractMovingMenu<TrashHandlerHelper.TrashHandler> {
    public static final int height = 180;
    public static final int width = 202;

    public ItemTrashCanMenu(@NotNull TrashHandlerHelper.TrashHandler handler, int pContainerId, @NotNull Player player) {
        super(handler, pContainerId, player);
    }

    public void changeWhiteOrBlack() {
        changeWhiteOrBlack(!getHandler().whiteOrBlack);
    }

    public void changeWhiteOrBlack(boolean whiteOrBlack) {
        getHandler().whiteOrBlack = whiteOrBlack;
    }

    public boolean whiteOrBlack() {
        return getHandler().whiteOrBlack;
    }

    @Override
    public void addPlayerSlots(@NotNull Player player) {
        for(int row = 0; row < 3; ++row) {
            for(int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(player.getInventory(), row * 9 + column + 9, 21 + 18 * column, height - 82 + 18 * row));
            }
        }

        for(int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(player.getInventory(), column, 21 + 18 * column, height - 24));
        }
    }

    @Override
    public void addSlots() {
        this.addSlot(new SlotItemHandler(StorageHandlerHelper.NULL_HANDLER, 0, 93, 25));

        for(int column = 0; column < 9; ++column) {
            this.addSlot(new SlotItemHandler(getHandler(), column, 8 + column * 18, 64) {
                public boolean mayPickup(Player playerIn) {
                    return false;
                }
            });
        }
    }
}
