package net.smartercontraptionstorage;

import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlock;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraftforge.fml.ModList;
import net.smartercontraptionstorage.AddActor.BackpackBehaviour;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.smartercontraptionstorage.AddStorage.FluidHander.FunctionalFluidHandlerHelper;
import net.smartercontraptionstorage.AddStorage.FluidHander.SBackPacksFluidHandlerHelper;
import net.smartercontraptionstorage.AddStorage.FluidHander.TrashcanFluidHelper;
import net.smartercontraptionstorage.AddStorage.ItemHandler.*;
import net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper.AEControllerBlock;
import net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper.AEEnergyBlock;
import net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper.MEStorageFilter;
import net.smartercontraptionstorage.Ponder.SCS_Ponder;
import net.smartercontraptionstorage.AddActor.ToolboxBehaviour;

import static net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper.register;
import static net.smartercontraptionstorage.AddStorage.FluidHander.FluidHandlerHelper.register;
import static net.smartercontraptionstorage.Ponder.SCS_Ponder.CONTROLLABLE_CONTAINERS;

@Mod(SmarterContraptionStorage.MODID)
public class SmarterContraptionStorage {
    public static final String MODID = "smartercontraptionstorage";
    public SmarterContraptionStorage() {
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SmarterContraptionStorageConfig.SPEC,"Smarter_Contraption_Storage.toml");
    }
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        ModList list = ModList.get();
        if(list.isLoaded("create")){
            register(new ToolboxHandlerHelper());
            for (BlockEntry<ToolboxBlock> toolboxBlockBlockEntry : AllBlocks.TOOLBOXES) {
                AllMovementBehaviours.registerBehaviour(toolboxBlockBlockEntry.get(), new ToolboxBehaviour());
            }
            if(list.isLoaded("trashcans")) {
                register(new TrashHandlerHelper());
                register(new TrashcanFluidHelper());
            }
            if(list.isLoaded("storagedrawers")) {
                register(new DrawersHandlerHelper());
                register(new CompactingHandlerHelper());
            }
            if(list.isLoaded("sophisticatedbackpacks")){
                register(new SBackPacksHandlerHelper());
                register(new SBackPacksFluidHandlerHelper());
                BackpackBehaviour behaviour = new BackpackBehaviour();
                AllMovementBehaviours.registerBehaviour(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.BACKPACK.get(),behaviour);
                AllMovementBehaviours.registerBehaviour(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.COPPER_BACKPACK.get(),behaviour);
                AllMovementBehaviours.registerBehaviour(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.IRON_BACKPACK.get(),behaviour);
                AllMovementBehaviours.registerBehaviour(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.GOLD_BACKPACK.get(),behaviour);
                AllMovementBehaviours.registerBehaviour(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.DIAMOND_BACKPACK.get(),behaviour);
                AllMovementBehaviours.registerBehaviour(net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.NETHERITE_BACKPACK.get(),behaviour);
            }
            if(list.isLoaded("functionalstorage")){
                register(new FunctionalDrawersHandlerHelper());
                register(new FunctionalCompactingHandlerHelper());
                register(new FunctionalFluidHandlerHelper());
            }
            if(SmarterContraptionStorageConfig.AE2SUPPORT.get() && list.isLoaded("ae2")){
                register(new AE2BusBlockHelper());
                register(new MEStorageFilter());
                register(new AEControllerBlock());
                register(new AEEnergyBlock());
                register(new SpatialHandler());
            }
            if(list.isLoaded("cobblefordays"))
                register(new CobblestoneGenerator());
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            ModList list = ModList.get();
            if(list.isLoaded("create")){
                SCS_Ponder.register();
                if(list.isLoaded("trashcans"))
                    SCS_Ponder.registerTrashCan();
                if(list.isLoaded("storagedrawers"))
                    PonderRegistry.TAGS.forTag(CONTROLLABLE_CONTAINERS).add(ModBlocks.CONTROLLER.get());
                if(SmarterContraptionStorageConfig.AE2SUPPORT.get() && list.isLoaded("ae2"))
                    SCS_Ponder.registerAE();
            }
        }
    }
}
