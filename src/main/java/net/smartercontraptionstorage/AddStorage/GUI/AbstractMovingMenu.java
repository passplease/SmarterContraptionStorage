package net.smartercontraptionstorage.AddStorage.ItemHandler;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractMovingMenu<T extends ItemStackHandler & MovingMenuProvider> extends AbstractContainerMenu {
    @NotNull protected final T handler;

    protected AbstractMovingMenu(@NotNull T handler, int pContainerId,@NotNull Player player) {
        super(handler.getMenuType(), pContainerId);
        this.handler = handler;
        addSlots();
        addPlayerSlots(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot clickedSlot = this.getSlot(index);
        if (!clickedSlot.hasItem()) {
            return ItemStack.EMPTY;
        } else {
            ItemStack stack = clickedSlot.getItem();
            int size = getHandler().getSlots();
            boolean success = false;
            if (index < size) {
                success = !this.moveItemStackTo(stack, size, this.slots.size(), false);
                getHandler().setStackInSlot(index,stack);
            } else {
                success = !this.moveItemStackTo(stack, 0, size - 1, false);
            }

            return success ? ItemStack.EMPTY : stack;
        }
    }

    public abstract void addPlayerSlots(@NotNull Player player);

    public abstract void addSlots();

    public @NotNull T getHandler() {
        return handler;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return getHandler().stillValid(player);
    }
}