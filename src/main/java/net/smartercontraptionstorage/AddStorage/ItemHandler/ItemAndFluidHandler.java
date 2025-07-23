package net.smartercontraptionstorage.AddStorage.ItemHandler;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public interface ItemAndFluidHandler extends IItemHandler, IFluidHandler {
    boolean isItemHandler();

    boolean isFluidHandler();

    @Override
    default int getSlots(){
        return isItemHandler() ? slotCount() : 0;
    }

    @Override
    default @NotNull ItemStack getStackInSlot(int slot){
        uncheckHandlerCall(true,false);
        return ItemStack.EMPTY;
    }

    @Override
    default int getSlotLimit(int slot){
        uncheckHandlerCall(true,false);
        return 0;
    }

    @Override
    default boolean isItemValid(int slot, @NotNull ItemStack stack){
        uncheckHandlerCall(true,false);
        return false;
    }

    @Override
    default int getTanks(){
        uncheckHandlerCall(false,true);
        return isFluidHandler() ? slotCount() : 0;
    }

    int slotCount();

    default boolean validSlot(int slot){
        return slot >= 0 && slot < slotCount();
    }

    @Override
    default @NotNull FluidStack getFluidInTank(int tank){
        uncheckHandlerCall(false,true);
        return FluidStack.EMPTY;
    }

    @Override
    default int getTankCapacity(int tank){
        uncheckHandlerCall(false,true);
        return 0;
    }

    @Override
    default int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
        uncheckHandlerCall(false,true);
        return 0;
    }

    @Override
    default @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
        uncheckHandlerCall(false,true);
        return FluidStack.EMPTY;
    }

    @Override
    default @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
        uncheckHandlerCall(false,true);
        return FluidStack.EMPTY;
    }

    @Override
    default @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        uncheckHandlerCall(true,false);
        return stack;
    }

    @Override
    default @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        uncheckHandlerCall(true,false);
        return ItemStack.EMPTY;
    }

    @Override
    default boolean isFluidValid(int tank, @NotNull FluidStack stack){
        uncheckHandlerCall(false,true);
        return false;
    }

    default void uncheckHandlerCall(boolean isItemHandler, boolean isFluidHandler) {
        String error = "";
        if(isItemHandler && !isItemHandler())
            error += "This is not an item handler !";
        if(isFluidHandler && !isFluidHandler())
            error += "This is not a fluid handler !";
        if(!error.isEmpty())
            throw new IllegalCallerException(error);
    }
}
