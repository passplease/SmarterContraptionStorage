package net.smartercontraptionstorage.AddStorage.GUI;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.smartercontraptionstorage.AddStorage.ItemHandler.FunctionalCompactingHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class MovingFunctionalCompactingMenu extends AbstractMovingMenu<FunctionalCompactingHandlerHelper.FCDrawersHandler>{
    public MovingFunctionalCompactingMenu(FunctionalCompactingHandlerHelper.@NotNull FCDrawersHandler handler, int pContainerId, @NotNull Player player) {
        super(handler, pContainerId, player);
    }

    public MovingFunctionalCompactingMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        super(id, inventory, buf, (buffer) -> new FunctionalCompactingHandlerHelper.FCDrawersHandler(buffer.readNbt()));
    }

    @Override
    public void addPlayerSlots(@NotNull Player player) {
        addPlayerSlots(player.getInventory(),8,102);
    }

    @Override
    public void addSlots() {
        addSlot(UnchangeableSlot.create(getHandler().upgrades.get(0),10,70));
        addSlot(UnchangeableSlot.create(getHandler().upgrades.get(1),28,70));
        addSlot(UnchangeableSlot.create(getHandler().upgrades.get(2),46,70));
        addSlot(UnchangeableSlot.create(getHandler().upgrades.get(3),114,70));
        addSlot(UnchangeableSlot.create(getHandler().upgrades.get(4),132,70));
        addSlot(UnchangeableSlot.create(getHandler().upgrades.get(5),150,70));
    }

    @Override
    public int getMenuSlots() {
        return 6;
    }

    public Object throwSlotCountError(){
        throw new IllegalArgumentException("Invalid Drawer slots count of " + getHandlerSlot() + " !");
    }
}
