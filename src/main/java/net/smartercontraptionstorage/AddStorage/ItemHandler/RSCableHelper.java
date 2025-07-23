package net.smartercontraptionstorage.AddStorage.ItemHandler;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.energy.EnergyNetworkComponent;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.common.content.Blocks;
import com.refinedmods.refinedstorage.common.content.DataComponents;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.controller.ControllerBlockEntity;
import com.refinedmods.refinedstorage.common.grid.WirelessGridItem;
import com.refinedmods.refinedstorage.common.iface.InterfaceBlockEntity;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerData;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.smartercontraptionstorage.AddStorage.NeedDealWith;
import net.smartercontraptionstorage.Mixin.RS.AbstractBaseNetworkNodeContainerBlockEntityMixin;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface RSCableHelper<T> extends NeedDealWith, ItemAndFluidHandler {
    Actor ACTOR = new ContraptionActor();

    @NotNull StorageNetworkComponent getStorage();

    @NotNull EnergyNetworkComponent getEnergy();

    boolean canWork();

    boolean hasFilter();

    @NotNull List<ResourceKey> getFilter();

    void setCanWork(boolean canWork);

    void setFilter(@NotNull List<InterfaceBlockEntity> interfaces);

    ResourceKey getKey(T t);

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
        Blocks.INSTANCE.getImporter().values().forEach(register);
        Blocks.INSTANCE.getExporter().values().forEach(register);
    }

    static <T> T create(@NotNull ResourceContainerData data,@NotNull MinecraftServer server, T defaultValue, BiFunction<StorageNetworkComponent, EnergyNetworkComponent, T> creator){
        for(Optional<ResourceAmount> filter : data.resources()){
            if(
                    filter.isPresent() &&
                    filter.get().resource() instanceof ItemResource item &&
                    item.item() instanceof WirelessGridItem &&
                    Items.INSTANCE.getWirelessGrid().isBound(item.toItemStack())
            ){
                Optional<? extends GlobalPos> pos = item.components().get(DataComponents.INSTANCE.getNetworkLocation());
                if(pos != null && pos.isPresent()){
                    return create(pos.get(),server,defaultValue,creator);
                }
            }
        }
        return defaultValue;
    }

    private static <T> T create(GlobalPos bindPos, MinecraftServer server, T defaultValue, BiFunction<@NotNull StorageNetworkComponent, @NotNull EnergyNetworkComponent, T> creator) {
        ServerLevel level = Utils.getLevel(server, bindPos.dimension());
        if(
                level != null &&
                level.getBlockEntity(bindPos.pos()) instanceof ControllerBlockEntity controller &&
                ((AbstractBaseNetworkNodeContainerBlockEntityMixin)controller).isActive() &&
                controller.getNetworkForItem() != null
        ){
            try{
                StorageNetworkComponent storage = controller.getNetworkForItem().getComponent(StorageNetworkComponent.class);
                EnergyNetworkComponent energy = controller.getNetworkForItem().getComponent(EnergyNetworkComponent.class);
                return creator.apply(storage,energy);
            }catch(Exception ignored){}
        }
        return defaultValue;
    }

    static boolean checkUpgrades(List<ItemStack> upgrades, Item target, int count) {
        return upgrades.stream().filter(stack -> stack.is(target)).count() >= count;
    }

    static boolean checkUpgrades(List<ItemStack> upgrades, Item target) {
        return checkUpgrades(upgrades, target, upgrades.size());
    }

    static Action valueOf(boolean simulate){
        return simulate ? Action.SIMULATE : Action.EXECUTE;
    }

    /**
     * @return Return inserted count
     * */
    default long insert(T t,long amount,boolean simulate){
        if(!useEnergy())
            return 0;
        ResourceKey key = getKey(t);
        return allowedKey(key) ? getStorage().insert(key,amount,valueOf(simulate),ACTOR) : 0;
    }

    /**
     * @return Return extracted count
     * */
    default long extract(T t, long amount, boolean simulate){
        if(!useEnergy())
            return 0;
        ResourceKey key = getKey(t);
        return allowedKey(key) ? getStorage().extract(key,amount,valueOf(simulate),ACTOR) : 0;
    }

    default boolean allowedKey(ResourceKey key){
        return !hasFilter() || getFilter().contains(key);
    }

    default int slotCount(){
        return getStorage().getAll().size();
    }

    @Override
    default int getSlots(){
        return isItemHandler() ? slotCount() : 0;
    }

    @Override
    default @NotNull ItemStack getStackInSlot(int slot) {
        if(isItemHandler() && validSlot(slot)){
            ResourceAmount resource = getStorage().getAll().stream().toList().get(slot);
            if(resource.resource() instanceof ItemResource item && allowedKey(item)) {
                return item.toItemStack(resource.amount());
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    default int getSlotLimit(int slot){
        if(isItemHandler() && validSlot(slot)){
            return Math.toIntExact(getStorage().get(ItemResource.ofItemStack(getStackInSlot(slot))));
        }
        return 0;
    }

    @Override
    default boolean isItemValid(int slot, @NotNull ItemStack stack){
        return isItemHandler() && validSlot(slot) && !getStackInSlot(slot).isEmpty() && getStorage().contains(ItemResource.ofItemStack(getStackInSlot(slot)));
    }

    @Override
    default int getTanks(){
        return isFluidHandler() ? slotCount() : 0;
    }

    @Override
    default @NotNull FluidStack getFluidInTank(int tank){
        if(isFluidHandler() && validSlot(tank)){
            ResourceAmount resource = getStorage().getAll().stream().toList().get(tank);
            if(resource.resource() instanceof FluidResource fluid && allowedKey(fluid)) {
                return new FluidStack(fluid.fluid(),Math.toIntExact(resource.amount()));
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    default int getTankCapacity(int tank) {
        FluidStack fluid = getFluidInTank(tank);
        return isFluidHandler() && validSlot(tank) ? Math.toIntExact(getStorage().get(new FluidResource(fluid.getFluid(),fluid.getComponentsPatch()))) : 0;
    }

    @Override
    default boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return isFluidHandler() && validSlot(tank) && getStorage().contains(new FluidResource(stack.getFluid(),stack.getComponentsPatch()));
    }

    default boolean useEnergy(){
        return getEnergy().extract(100) >= 100;
    }

    @Override
    default boolean selfCheck() {
        return canWork();
    }

    class ContraptionActor implements Actor{
        @Override
        public @NotNull String getName() {
            return "contraption actor";
        }
    }
}
