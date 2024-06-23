package net.SmarterContraptionStorage.FunctionInterface;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class a extends ItemStackHandler {
    int[] limits;
    @Override
    public void setSize(int size) {
        super.setSize(size);
        limits = new int[size];
    }
    @Override
    public int getSlotLimit(int slot) {
        return limits[slot];
    }
    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        super.setStackInSlot(slot, stack);
        limits[slot] = stack.getMaxStackSize();
    }
}
