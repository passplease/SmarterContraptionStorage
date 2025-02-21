package net.smartercontraptionstorage.AddStorage.GUI;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.smartercontraptionstorage.AddStorage.ItemHandler.FunctionalCompactingHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class MovingFCompactingDrawerMenu extends AbstractMovingMenu<FunctionalCompactingHandlerHelper.FCDrawersHandler>{
    public MovingFCompactingDrawerMenu(FunctionalCompactingHandlerHelper.@NotNull FCDrawersHandler handler, int pContainerId, @NotNull Player player) {
        super(handler, pContainerId, player);
    }

    public MovingFCompactingDrawerMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        super(id, inventory, buf, (buffer) -> new FunctionalCompactingHandlerHelper.FCDrawersHandler(buffer.readNbt()));
    }

    @Override
    public void addPlayerSlots(@NotNull Player player) {

    }

    @Override
    public void addSlots() {

    }

    @Override
    public int getMenuSlots() {
        return 0;
    }
}
