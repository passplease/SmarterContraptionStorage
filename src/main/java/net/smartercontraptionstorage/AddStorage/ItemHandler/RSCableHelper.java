package net.smartercontraptionstorage.AddStorage.ItemHandler;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.blockentity.ControllerBlockEntity;
import com.refinedmods.refinedstorage.blockentity.InterfaceBlockEntity;
import com.refinedmods.refinedstorage.item.NetworkItem;
import com.refinedmods.refinedstorage.item.WirelessGridItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.smartercontraptionstorage.AddStorage.NeedDealWith;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Can not move RS Blocks, so this is useless
 * */

@Deprecated
public interface RSCableHelper<T> extends NeedDealWith, ItemAndFluidHandler {
    @NotNull INetwork getStorage();

    @NotNull IEnergyStorage getEnergy();

    boolean canWork();

    boolean hasFilter();

    @NotNull List<T> getFilter();

    void setCanWork(boolean canWork);

    void setFilter(@NotNull List<InterfaceBlockEntity> interfaces);

    long insert(@Nonnull T t, int amount, Action action);

    /**
     * @param flags 1 means compare NBT, 0 means don't do that.
     * */
    long extract(@Nonnull T t, int amount, int flags, Action action);

    @Override
    default void doSomething(BlockEntity entity){}

    @Override
    default void finallyDo(){
        boolean canWork = false;
        List<InterfaceBlockEntity> interfaces = new ArrayList<>();
        for(BlockEntity entity : StorageHandlerHelper.BlockEntityList){
            if(entity instanceof InterfaceBlockEntity && !interfaces.contains(entity))
                interfaces.add((InterfaceBlockEntity) entity);
            else if(entity instanceof ControllerBlockEntity)
                canWork = true;
            else if(entity.getClass().getPackage().getName().startsWith("appeng.blockentity")){
                setCanWork(false);
                return;
            }
        }
        setCanWork(canWork);
        if(!interfaces.isEmpty() && hasFilter())
            setFilter(interfaces);
    }

    static void register(Consumer<Block> register){
        RSBlocks.IMPORTER.stream().forEach(register);
        RSBlocks.EXPORTER.stream().forEach(register);
    }

    static <T> T create(@NotNull IItemHandlerModifiable data, @NotNull MinecraftServer server, T defaultValue, BiFunction<INetwork, IEnergyStorage, T> creator){
        for (int slot = 0; slot < data.getSlots(); slot++) {
            ItemStack stack = data.getStackInSlot(slot);
            if(!stack.isEmpty() && stack.getItem() instanceof WirelessGridItem && NetworkItem.isValid(stack)){
                GlobalPos pos = GlobalPos.of(Objects.requireNonNull(NetworkItem.getDimension(stack)),new BlockPos(NetworkItem.getX(stack),NetworkItem.getY(stack),NetworkItem.getZ(stack)));
                return create(pos,server,defaultValue,creator);
            }
        }
        return defaultValue;
    }

    private static <T> T create(GlobalPos bindPos, MinecraftServer server, T defaultValue, BiFunction<INetwork, IEnergyStorage, T> creator) {
        ServerLevel level = Utils.getLevel(server, bindPos.dimension());
        if(
                level != null &&
                level.getBlockEntity(bindPos.pos()) instanceof ControllerBlockEntity controller
        ){
            INetwork network = controller.getNetwork();
            if(network != null && network.canRun())
                return creator.apply(network,network.getEnergyStorage());
        }
        return defaultValue;
    }

    static boolean checkUpgrades(IItemHandler upgrades, Item target, int size) {
        int count = 0;
        for(int i = 0; i < upgrades.getSlots() - 1; i++) {
            ItemStack item = upgrades.getStackInSlot(i);
            if(item.is(target))
                count++;
        }
        return count >= size;
    }

    static boolean checkUpgrades(IItemHandler upgrades, Item target) {
        return checkUpgrades(upgrades, target, upgrades.getSlots());
    }

    static Action valueOf(boolean simulate){
        return simulate ? Action.SIMULATE : Action.PERFORM;
    }

    /**
     * @return Return inserted count
     * */
    default long insert(T t,long amount,boolean simulate){
        if(!useEnergy())
            return 0;
        return allowedKey(t) ? insert(t,(int)amount,valueOf(simulate)) : 0;
    }

    /**
     * @return Return extracted count
     * */
    default long extract(T t, long amount, boolean simulate){
        if(!useEnergy())
            return 0;
        return allowedKey(t) ? extract(t, (int) amount,1,valueOf(simulate)) : 0;
    }

    default boolean allowedKey(T key){
        return !hasFilter() || getFilter().contains(key);
    }

    default int slotCount(){
        if(isItemHandler()) {
            return getStorage().getItemStorageCache().getList().getStacks().size();
        } else if(isFluidHandler()){
            return getStorage().getFluidStorageCache().getList().getStacks().size();
        }
        return 0;
    }

    @Override
    default int getSlots(){
        return isItemHandler() ? slotCount() : 0;
    }

    @Override
    default @NotNull ItemStack getStackInSlot(int slot) {
        if(isItemHandler() && validSlot(slot)){
            return getStorage().getItemStorageCache().getList().getStacks().stream().toList().get(slot).getStack();
        }
        return ItemStack.EMPTY;
    }

    @Override
    default int getSlotLimit(int slot){
        if(isItemHandler() && validSlot(slot)){
            return Math.toIntExact(getStackInSlot(slot).getMaxStackSize());
        }
        return 0;
    }

    @Override
    default boolean isItemValid(int slot, @NotNull ItemStack stack){
        ItemStack itemStack = getStackInSlot(slot);
        return isItemHandler() && validSlot(slot) && (ItemStack.isSameItemSameTags(itemStack,stack) || itemStack.isEmpty());
    }

    @Override
    default int getTanks(){
        return isFluidHandler() ? slotCount() : 0;
    }

    @Override
    default @NotNull FluidStack getFluidInTank(int tank){
        if(isFluidHandler() && validSlot(tank)){
            return getStorage().getFluidStorageCache().getList().getStacks().stream().toList().get(tank).getStack();
        }
        return FluidStack.EMPTY;
    }

    @Override
    default int getTankCapacity(int tank) {
        return isFluidHandler() && validSlot(tank) ? getFluidInTank(tank).getAmount() : 0;
    }

    @Override
    default boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return isFluidHandler() && validSlot(tank) ;
    }

    default boolean useEnergy(){
        if(getEnergy().extractEnergy(100,true) >= 100){
            getEnergy().extractEnergy(100,false);
            return true;
        }
        return false;
    }

    @Override
    default boolean selfCheck() {
        return canWork();
    }
}
