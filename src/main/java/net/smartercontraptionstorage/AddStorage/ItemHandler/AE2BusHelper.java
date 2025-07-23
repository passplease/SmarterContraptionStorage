package net.smartercontraptionstorage.AddStorage.ItemHandler;

import appeng.api.config.Actionable;
import appeng.api.implementations.parts.ICablePart;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.parts.IPart;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import appeng.api.util.AECableType;
import appeng.blockentity.misc.InterfaceBlockEntity;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.blockentity.networking.ControllerBlockEntity;
import appeng.blockentity.networking.CreativeEnergyCellBlockEntity;
import appeng.blockentity.networking.EnergyCellBlockEntity;
import appeng.blockentity.spatial.SpatialIOPortBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.items.tools.powered.WirelessCraftingTerminalItem;
import appeng.me.GridNode;
import appeng.me.service.EnergyService;
import appeng.parts.automation.ExportBusPart;
import appeng.parts.automation.IOBusPart;
import appeng.util.ConfigInventory;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.smartercontraptionstorage.AddStorage.NeedDealWith;
import net.smartercontraptionstorage.Interface.TriFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

public interface AE2BusHelper extends NeedDealWith, ItemAndFluidHandler {
    boolean getCanWork();

    void setCanWork(boolean canWork);

    boolean hasFilter();

    void setFilter(boolean hasFilter);

    @Nullable IGridNode getExtractNode();

    @Nullable IGridNode getImportNode();

    ArrayList<AEKey> getExtractKeys();

    AE2ContraptionSource getExtractSource();

    AE2ContraptionSource getImportSource();

    @Override
    default void doSomething(BlockEntity entity) {
        StorageHandlerHelper.BlockEntityList.add(entity);
    }

    default @Nullable EnergyService getEnergy(boolean extractOrImport){
        IGrid grid = getGrid(extractOrImport);
        if(grid == null)
            return null;
        if(grid.getEnergyService() instanceof EnergyService service)
            return service;
        else return null;
    }

    default boolean useEnergy(boolean extractOrImport,boolean simulate){
        EnergyService provider = getEnergy(extractOrImport);
        boolean back = false;
        if(provider != null) {
            double use = provider.getChannelPowerUsage() * 5;
            back = Double.compare(provider.extractProviderPower(use, Actionable.ofSimulate(simulate)),use) == 0;
        }
        return back;
    }

    default boolean canWork(boolean extractOrImport, boolean simulate){
        refreshStack(getStorage(extractOrImport));
        if(!getCanWork())
            return false;
        if(extractOrImport){
            if(getExtractNode() == null)
                return false;
            return getExtractNode().isActive() && useEnergy(true,simulate);
        }else {
            if(getImportNode() == null)
                return false;
            return getImportNode().isActive() && useEnergy(false,simulate);
        }
    }

    default @Nullable IGrid getGrid(boolean extractOrImport){
        if(extractOrImport)
            return getExtractNode() == null ? null : getExtractNode().getGrid();
        else
            return getImportNode() == null ? null : getImportNode().getGrid();
    }


    default void refreshStack(@Nullable MEStorage extractStorage){// synchronize stored items in AE Net
        if(hasFilter())
            return;
        getExtractKeys().clear();
        if(extractStorage != null)
            getExtractKeys().addAll(extractStorage.getAvailableStacks().keySet());
    }

    default @Nullable MEStorage getStorage(boolean extractOrImport){
        IGrid grid = getGrid(extractOrImport);
        if(grid == null)
            return null;
        return grid.getStorageService().getInventory();
    }

    default boolean onlyInsert(){
        if(getExtractNode() == null){
            if(getImportNode() == null) {
                setCanWork(false);
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    default int slotCount(){
        refreshStack(getStorage(true));
        return onlyInsert() ? 1 : getExtractKeys().size();
    }

    default int getSlots(){
        return slotCount();
    }

    default long insert(AEKey key, int count, Actionable action){
        if(canWork(false,action.isSimulate())){
            MEStorage importStorage = getStorage(false);
            if (importStorage == null)
                return 0;
            return importStorage.insert(key,count,action,getImportSource());
        }
        return 0;
    }

    default long extract(AEKey key,int amount,Actionable action){
        if(canWork(true,action.isSimulate())) {
            MEStorage extractStorage = getStorage(true);
            if (extractStorage == null)
                return 0;
            return extractStorage.extract(key, amount, action, getExtractSource());
        }
        return 0;
    }

    default @NotNull CompoundTag serializeNBT(@NotNull HolderLookup.Provider provider,@NotNull CompoundTag tag) {
        if(getCanWork()) {
            if (getImportNode() instanceof GridNode node) {
                node.saveToNBT("importNode", tag);
            }
            if (getExtractNode() instanceof GridNode node) {
                node.saveToNBT("extractNode", tag);
            }
            ListTag keys = new ListTag();
            getExtractKeys().forEach((key) -> keys.add(key.toTagGeneric(provider)));
            tag.put("extractKeys", keys);
            tag.putBoolean("hasFilter", hasFilter());
            tag.putInt("size",getSlots());
        }
        return tag;
    }

    static <T> T createHandler(BlockEntity entity, T defaultValue, TriFunction<Integer,IGridNode,IGridNode,T> creator){
        CableBusBlockEntity bus = (CableBusBlockEntity)entity;
        ICablePart center = (ICablePart)bus.getPart(null);
        if (center != null && center.getCableConnectionType() == AECableType.COVERED) {
            IGridNode exportHost = null;
            IGridNode importHost = null;

            for(IPart part : AE2BusHelper.getAllPart(bus)) {
                if (part instanceof IOBusPart) {
                    if (!AE2BusHelper.checkUpgrade(((IOBusPart)part).getUpgrades(), AEItems.SPEED_CARD.asItem(), 3)) {
                        continue;
                    }

                    ConfigInventory config = ((IOBusPart)part).getConfig();
                    AEKey key = config.getKey(0);
                    if (key != null) {
                        ItemStack item = key.wrapForDisplayOrFilter();
                        if (item.getItem() instanceof WirelessCraftingTerminalItem terminal) {
                            if (!AE2BusHelper.checkUpgrade(terminal.getUpgrades(item), AEItems.ENERGY_CARD.asItem())) {
                                continue;
                            }

                            IGrid id = terminal.getLinkedGrid(item, entity.getLevel(), null);
                            if (id != null) {
                                IGridNode host = id.getPivot();
                                if (host == null) {
                                    continue;
                                }

                                if (part instanceof ExportBusPart) {
                                    exportHost = host;
                                } else {
                                    importHost = host;
                                }
                            }
                        }
                    }
                }

                if (exportHost != null && importHost != null) {
                    return create(exportHost,importHost,creator);
                }
            }

            return exportHost == null && importHost == null ? defaultValue : create(importHost,importHost,creator);
        } else {
            return defaultValue;
        }
    }

    static <T> T create(@Nullable IGridNode extractNode, @Nullable IGridNode importNode, TriFunction<Integer,IGridNode,IGridNode,T> creator) {
        IStorageService extractNet = null;
        if (extractNode != null) {
            extractNet = extractNode.getGrid().getStorageService();
        }

        int size = extractNet == null ? 1 : extractNet.getInventory().getAvailableStacks().size();
        return creator.function(size,extractNode,importNode);
    }

    static IPart[] getAllPart(CableBusBlockEntity bus){
        IPart[] iParts = new IPart[6];
        iParts[0] = bus.getPart(Direction.NORTH);
        iParts[1] = bus.getPart(Direction.SOUTH);
        iParts[2] = bus.getPart(Direction.EAST);
        iParts[3] = bus.getPart(Direction.WEST);
        iParts[4] = bus.getPart(Direction.UP);
        iParts[5] = bus.getPart(Direction.DOWN);
        return iParts;
    }

    static boolean checkUpgrade(InternalInventory upgrade, Item targetUpgrade, int size) {
        int count = 0;
        for(int i = 0; i < upgrade.size() - 1; i++) {
            ItemStack item = upgrade.getStackInSlot(i);
            if(item.is(targetUpgrade))
                count++;
        }
        return count >= size;
    }

    static boolean checkUpgrade(InternalInventory upgrade, Item targetUpgrade) {
        return checkUpgrade(upgrade, targetUpgrade, upgrade.size() - 1);
    }

    static void registerBlock(Consumer<Block> register) {
        register.accept(AEBlocks.CABLE_BUS.block());
    }

    @Override
    default void finallyDo() {
        boolean controller = false,energy = false;
        for(BlockEntity entity : StorageHandlerHelper.BlockEntityList) {
            if (entity instanceof InterfaceBlockEntity MEInterface) {
                getExtractKeys().addAll(MEInterface.getInterfaceLogic().getConfig().getAvailableStacks().keySet());
            } else if (controller || entity instanceof ControllerBlockEntity)
                controller = true;
            else if(energy || entity instanceof EnergyCellBlockEntity || entity instanceof CreativeEnergyCellBlockEntity)
                energy = true;
            else if(entity instanceof SpatialIOPortBlockEntity || entity.getClass().getPackage().getName().startsWith("com.refinedmods.refinedstorage.common")){
                setCanWork(false);
                return;
            }
        }
        setCanWork(controller && energy);
        setFilter(!getExtractKeys().isEmpty());
    }

    @Override
    default boolean selfCheck() {
        return canWork(true, true) || canWork(false, true);
    }

    record AE2ContraptionSource(IActionHost host) implements IActionSource {
        public static AE2ContraptionSource create(IGridNode extractNode) {
            return new AE2ContraptionSource(() -> extractNode);
        }
        @Override
        public Optional<Player> player() {
            return Optional.empty();
        }

        @Override
        public Optional<IActionHost> machine() {
            return Optional.of(host);
        }

        @Override
        public <T> Optional<T> context(Class<T> aClass) {
            return Optional.empty();
        }
    }
}
