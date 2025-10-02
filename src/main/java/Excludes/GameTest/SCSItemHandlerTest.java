package Excludes.GameTest;

import appeng.api.implementations.items.ISpatialStorageCell;
import appeng.api.networking.IGridNode;
import appeng.api.parts.IPart;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.blockentity.networking.ControllerBlockEntity;
import appeng.blockentity.spatial.SpatialIOPortBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.parts.automation.IOBusPart;
import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.block.tile.CompactingDrawerTile;
import com.buuz135.functionalstorage.util.CompactingUtil;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.core.ModBlockEntities;
import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.content.BlockEntities;
import com.refinedmods.refinedstorage.common.support.network.AbstractBaseNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlock;
import com.simibubi.create.infrastructure.gametest.CreateGameTestHelper;
import com.simibubi.create.infrastructure.gametest.GameTestGroup;
import com.supermartijn642.trashcans.TrashCans;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.items.IItemHandler;
import net.smartercontraptionstorage.AddStorage.ItemHandler.AE2BusHelper;
import net.smartercontraptionstorage.AddStorage.ItemHandler.RSCableHelper;
import net.smartercontraptionstorage.AddStorage.ItemHandler.SpatialHandler;
import net.smartercontraptionstorage.Mixin.RS.AbstractBaseNetworkNodeContainerBlockEntityMixin;
import net.smartercontraptionstorage.SmarterContraptionStorage;
import net.smartercontraptionstorage.SmarterContraptionStorageConfig;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@GameTestGroup(path = "item_handler",namespace = SmarterContraptionStorage.MODID)
public class SCSItemHandlerTest {
    @GameTest(template = "trashcan",timeoutTicks = SCSGameTests.ONE_MINUTE * 2)
    public static void testTrashCan(CreateGameTestHelper helper) {
        BlockPos button = new BlockPos(1,6,2);
        BlockPos gearshift = new BlockPos(1,7,2);
        BlockPos chest = new BlockPos(1,4,3);
        BlockPos barrel = new BlockPos(3,6,4);
        BlockPos grassBlock = new BlockPos(1,2,3);
        BlockPos toolbox = new BlockPos(1,8,4);
        BlockPos trashcan = new BlockPos(1,7,4);
        AtomicInteger step = new AtomicInteger(1);

        helper.assertContainerEmpty(chest);
        helper.assertContainerEmpty(barrel);

        helper.useBlock(button.below());
        helper.succeedWhen(() -> {
            if(step.get() != 4) {
                contraptionStoped(helper,gearshift);
                switch(step.get()) {
                    case 1 -> {
                        helper.assertContainerContains(barrel, Items.DIRT);

                        // Prepare for next step
                        helper.destroyBlock(barrel);
                        helper.setBlock(barrel,Blocks.BARREL);
                        helper.destroyBlock(toolbox);
                        helper.setBlock(toolbox,Blocks.COBBLESTONE);
                    }
                    case 2 -> {
                        helper.assertContainerEmpty(barrel);

                        // Prepare for next step
                        helper.getBlockEntity(TrashCans.item_trash_can_tile,trashcan).itemFilterWhitelist = true;
                    }
                    case 3 -> helper.assertContainerContains(barrel, Items.DIRT);
                }
                helper.setBlock(grassBlock,Blocks.GRASS_BLOCK);
                helper.useBlock(button.below());
                helper.fail("Next step: " + step.incrementAndGet());
            }
        });
    }

    @GameTest(template = "drawer",timeoutTicks = CreateGameTestHelper.FIFTEEN_SECONDS)
    public static void testDrawer(CreateGameTestHelper helper) {
        defaultTest(helper);
    }

    @GameTest(template = "compacting_drawer",timeoutTicks = SCSGameTests.ONE_MINUTE)
    public static void testCompactingDrawer(CreateGameTestHelper helper) {
        BlockPos button = new BlockPos(1,6,2);
        BlockPos gearshift = new BlockPos(1,7,2);
        BlockPos barrel = new BlockPos(3,4,4);
        BlockPos drawer = new BlockPos(1,4,3);

        helper.assertContainerEmpty(barrel);
        helper.assertContainerEmpty(drawer);

        helper.useBlock(button.below());
        helper.succeedWhen(() -> {
            contraptionStoped(helper,gearshift);
            Object2LongMap<Item> items = helper.getItemContent(barrel);
            if(!items.containsKey(Items.IRON_INGOT) || items.getLong(Items.IRON_INGOT) != 9)
                helper.fail("Compacting drawer doesn't contain 9 iron ingots !");
            IDrawerGroup group = helper.getBlockEntity(ModBlockEntities.FRACTIONAL_DRAWERS_3.get(), drawer).getGroup();
            for(int slot = 0; slot < group.getDrawerCount(); slot++)
                if(group.getDrawer(slot).getStoredItemPrototype().isEmpty())
                    helper.fail("Didn't save compacting drawer filter !");
        });
    }

    @GameTest(template = "functional_drawer",timeoutTicks = CreateGameTestHelper.FIFTEEN_SECONDS)
    public static void testFunctionalDrawer(CreateGameTestHelper helper) {
        defaultTest(helper);
    }

    @GameTest(template = "functional_compacting_drawer",timeoutTicks = SCSGameTests.ONE_MINUTE)
    public static void testFunctionalCompactingDrawer(CreateGameTestHelper helper) {
        BlockPos button = new BlockPos(1,6,2);
        BlockPos gearshift = new BlockPos(1,7,2);
        BlockPos barrel = new BlockPos(3,4,4);
        BlockPos drawer = new BlockPos(1,4,3);

        helper.assertContainerEmpty(barrel);
        helper.assertContainerEmpty(drawer);

        helper.useBlock(button.below());
        helper.succeedWhen(() -> {
            contraptionStoped(helper,gearshift);
            Object2LongMap<Item> items = helper.getItemContent(barrel);
            if(!items.containsKey(Items.IRON_INGOT) || items.getLong(Items.IRON_INGOT) != 9)
                helper.fail("Compacting drawer doesn't contain 9 iron ingots !");
            BlockEntity blockEntity = helper.getBlockEntity(FunctionalStorage.COMPACTING_DRAWER.type().get(),drawer);
            if(blockEntity instanceof CompactingDrawerTile tile){
                for(CompactingUtil.Result filter : tile.handler.getResultList())
                    if(filter.getResult().isEmpty())
                        helper.fail("Didn't save compacting drawer filter!");
            }else helper.fail("Wrong drawer entity type !");
        });
    }

    /**
     * RS couldn't run game test. The structure could not be loaded. This test 100% will fail
     * */
    @GameTest(template = "rs_controller",required = false,setupTicks = CreateGameTestHelper.TEN_SECONDS,timeoutTicks = CreateGameTestHelper.FIFTEEN_SECONDS)
    public static void testRSController(CreateGameTestHelper helper) {
        BlockPos button = new BlockPos(2,6,2);
        BlockPos gearshift = new BlockPos(2,7,2);
        BlockPos grass = new BlockPos(2,2,3);
        GlobalPos wirelessAccessPoint = GlobalPos.of(helper.getLevel().dimension(),helper.absolutePos(new BlockPos(3, 10, 3)));
        com.refinedmods.refinedstorage.common.controller.ControllerBlockEntity controller = helper.getBlockEntity(BlockEntities.INSTANCE.getCreativeController(), new BlockPos(4, 10, 4));
        if(!((AbstractBaseNetworkNodeContainerBlockEntityMixin)controller).isActive() || controller.getNetworkForItem() == null)
            helper.fail("Controller block entity is not active !");
        StorageNetworkComponent storage = controller.getNetworkForItem().getComponent(StorageNetworkComponent.class);
        FluidResource water = new FluidResource(Fluids.WATER);
        ItemResource linerChassis = new ItemResource(AllBlocks.LINEAR_CHASSIS.asItem());
        ItemResource dirt = new ItemResource(Items.DIRT);
        ItemResource controllerItem = new ItemResource(com.refinedmods.refinedstorage.common.content.Blocks.INSTANCE.getController().getDefault().asItem());
        if(storage.contains(dirt) || !storage.contains(linerChassis) || !storage.contains(controllerItem))
            helper.fail("Wrong RS net storage !");
        BlockPos[] cables = {new BlockPos(2,7,5),new BlockPos(2,7,4)};
        String PosTag = "refinedstorage:network_location";
        for(BlockPos cable : cables){
            BlockEntity entity = helper.getBlockEntity(cable);
            if (entity instanceof AbstractBaseNetworkNodeContainerBlockEntity<?> port) {
                CompoundTag nbt = new CompoundTag();
                port.writeConfiguration(nbt,helper.getLevel().registryAccess());
                CompoundTag posTag = nbt.getCompound("rf").getCompound("s0").getCompound("resource").getCompound("components");
                posTag.put(PosTag,GlobalPos.CODEC.encode(wirelessAccessPoint, NbtOps.INSTANCE,new CompoundTag()).getOrThrow());
                port.readConfiguration(nbt,helper.getLevel().registryAccess());
                continue;
            }
            helper.fail("Wrong BlockEntity data !");
        }

        helper.useBlock(button.below());
        helper.succeedWhen(() -> {
            contraptionStoped(helper,gearshift);
            Collection<ResourceAmount> availableStacks = storage.getAll();
            List<ResourceKey> items = availableStacks.stream().map(ResourceAmount::resource).toList();
            List<Long> count = availableStacks.stream().map(ResourceAmount::amount).toList();
            if(SmarterContraptionStorageConfig.RSLoaded()) {
                if (!items.contains(dirt))
                    helper.fail("Dirt was not stored !");
                if(count.get(items.indexOf(linerChassis)) == 64)
                    helper.fail("Chassis was placed ! Filter may not work !");
                if(!items.contains(water))
                    helper.fail("AE Controller fluid handler not work !");
            }else{
                helper.assertItemEntityPresent(Items.DIRT,grass,1);
                if(count.get(items.indexOf(linerChassis)) != 64)
                    helper.fail("Chassis was been placed !");
                if(items.contains(water))
                    helper.fail("AE Controller fluid handler work !");
            }
            if(!items.contains(controllerItem))
                helper.fail("Controller was been placed !");
        });
    }

    @GameTest(template = "ae2_controller",setupTicks = CreateGameTestHelper.TEN_SECONDS,timeoutTicks = CreateGameTestHelper.FIFTEEN_SECONDS)
    public static void testAEController(CreateGameTestHelper helper) {
        BlockPos button = new BlockPos(2,6,2);
        BlockPos gearshift = new BlockPos(2,7,2);
        BlockPos grass = new BlockPos(2,2,3);
        GlobalPos wirelessAccessPoint = GlobalPos.of(helper.getLevel().dimension(),helper.absolutePos(new BlockPos(3, 10, 3)));
        ControllerBlockEntity controller = helper.getBlockEntity(AEBlocks.CONTROLLER.block().getBlockEntityType(), new BlockPos(4, 10, 4));
        IGridNode gridNode = controller.getGridNode();
        if(gridNode == null || !gridNode.isActive() || gridNode.getGrid() == null)
            helper.fail("Controller block entity is not active !");
        MEStorage storage = gridNode.getGrid().getStorageService().getInventory();
        AEFluidKey water = AEFluidKey.of(Fluids.WATER);
        AEItemKey linerChassis = AEItemKey.of(AllBlocks.LINEAR_CHASSIS);
        AEItemKey dirt = AEItemKey.of(Items.DIRT);
        AEItemKey controllerItem = AEItemKey.of(AEBlocks.CONTROLLER);
        Set<AEKey> aeKeys = storage.getAvailableStacks().keySet();
        if(aeKeys.contains(dirt) || !aeKeys.contains(linerChassis) || !aeKeys.contains(controllerItem))
            helper.fail("Wrong AE net storage !");
        BlockPos[] cables = {new BlockPos(2,7,5),new BlockPos(2,7,4)};
        cable:
        for(BlockPos cable : cables){
            CableBusBlockEntity bus = helper.getBlockEntity(AEBlocks.CABLE_BUS.block().getBlockEntityType(), cable);
            for(IPart part : AE2BusHelper.getAllPart(bus)){
                if (part instanceof IOBusPart){
                    AEKey k = ((IOBusPart) part).getConfig().getKey(0);
                    if (k instanceof AEItemKey key) {
                        ItemStack stack = key.getReadOnlyStack();
                        WirelessTerminalItem.LINKABLE_HANDLER.link(stack,wirelessAccessPoint);
                        continue cable;
                    }
                    helper.fail("Wrong cable settings");
                }
            }
        }

        helper.useBlock(button.below());
        helper.succeedWhen(() -> {
            contraptionStoped(helper,gearshift);
            KeyCounter availableStacks = storage.getAvailableStacks();
            if(SmarterContraptionStorageConfig.AE2Loaded()) {
                if (!availableStacks.keySet().contains(dirt))
                    helper.fail("Dirt was not stored !");
                if(availableStacks.get(linerChassis) == 64)
                    helper.fail("Chassis was placed ! Filter may not work !");
                if(availableStacks.get(water) == 0)
                    helper.fail("AE Controller fluid handler not work !");
            }else{
                helper.assertItemEntityPresent(Items.DIRT,grass,1);
                if(availableStacks.get(linerChassis) != 64)
                    helper.fail("Chassis was been placed !");
                if(availableStacks.get(water) != 0)
                    helper.fail("AE Controller fluid handler work !");
            }
            if(availableStacks.get(controllerItem) != 64)
                helper.fail("Controller was been placed !");
        });
    }

    @GameTest(template = "spatial",timeoutTicks = CreateGameTestHelper.FIFTEEN_SECONDS)
    public static void testSpatial(CreateGameTestHelper helper) {
        BlockPos button = new BlockPos(0,5,1);
        BlockPos gearshift = new BlockPos(0,6,1);
        BlockPos lever = new BlockPos(0,6,3);
        IItemHandler chest = helper.itemStorageAt(new BlockPos(1, 8, 5));
        ItemStack cobblestone = Items.COBBLESTONE.getDefaultInstance();
        cobblestone.setCount(Integer.MAX_VALUE);
        for (int slot = 0; slot < chest.getSlots(); slot++)
            chest.insertItem(slot,cobblestone,false);
        helper.useBlock(lever.east(2));
        helper.whenSecondsPassed(1,() -> {
            SpatialIOPortBlockEntity ioPort = helper.getBlockEntity(AEBlocks.SPATIAL_IO_PORT.block().getBlockEntityType(), new BlockPos(0, 6, 4));
            ItemStack stack = ioPort.getInternalInventory().getStackInSlot(1);
            if(stack.getItem() instanceof ISpatialStorageCell cell){
                SpatialHandler.SpatialHelper handlerHelper = SpatialHandler.SpatialHelper.create(cell.getAllocatedPlotId(stack));
                handlerHelper.setWork();
                for (int slot = 0; slot < handlerHelper.getSlots(); slot++){
                    if(handlerHelper.getStackInSlot(slot).is(Blocks.COBBLESTONE.asItem()) || handlerHelper.getStackInSlot(slot).isEmpty())
                        continue;
                    helper.fail("Wrong inventory slot: " + slot + " !");
                }
                helper.useBlock(button.below());
                helper.succeedWhen(() -> {
                    contraptionStoped(helper,gearshift);
                    boolean hasDirt = false;
                    for (int slot = 0; slot < handlerHelper.getSlots(); slot++) {
                        if(handlerHelper.getStackInSlot(slot).is(Items.DIRT)) {
                            hasDirt = true;
                            break;
                        }
                    }
                    if(SmarterContraptionStorageConfig.AE2Loaded()) {
                        if (!hasDirt) {
                            helper.fail("Dirt is not stored !");
                        }
                        helper.assertBlockPresent(Blocks.COBBLESTONE, 1, 5, 7);
                    }else {
                        if(hasDirt) {
                            helper.fail("Dirt is stored !");
                        }
                        helper.assertBlockNotPresent(Blocks.COBBLESTONE, 1, 6, 7);
                    }
                });
            }else helper.fail("Spatial is not stored !");
        });
    }

    @GameTest(template = "ender_chest",timeoutTicks = CreateGameTestHelper.FIFTEEN_SECONDS)
    public static void testEnderChest(CreateGameTestHelper helper) {
        defaultTest(helper);
    }

    public static void defaultTest(CreateGameTestHelper helper){
        BlockPos button = new BlockPos(1,6,2);
        BlockPos gearshift = new BlockPos(1,7,2);
        BlockPos barrel = new BlockPos(3,4,4);
        BlockPos container = new BlockPos(1,4,3);

        helper.assertContainerEmpty(barrel);
        helper.assertContainerEmpty(container);

        helper.useBlock(button.below());
        helper.succeedWhen(() -> {
            contraptionStoped(helper,gearshift);
            helper.assertContainerContains(barrel, Items.DIRT);
        });
    }

    public static void contraptionStoped(CreateGameTestHelper helper,BlockPos gearshift){
        helper.assertBlockProperty(gearshift, SequencedGearshiftBlock.STATE, 0);
    }
}
