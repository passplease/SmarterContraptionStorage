package net.smartercontraptionstorage;

import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.smartercontraptionstorage.AddStorage.FluidHander.*;
import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.MovingBlockEntityMenu;
import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.MovingBlockEntityScreen;
import net.smartercontraptionstorage.AddStorage.GUI.NormalMenu.*;
import net.smartercontraptionstorage.AddStorage.ItemHandler.*;
import net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper.*;
import net.smartercontraptionstorage.Ponder.SCS_Ponder;
import net.smartercontraptionstorage.Render.Overlay;

import java.awt.*;

import static net.smartercontraptionstorage.AddStorage.FluidHander.FluidHandlerHelper.register;
import static net.smartercontraptionstorage.SmarterContraptionStorage.*;

@Mod(value = SmarterContraptionStorage.MODID, dist = Dist.CLIENT)
public class SmarterContraptionStorageClient {
    public SmarterContraptionStorageClient(FMLModContainer container, IEventBus modEventBus) {
        SmarterContraptionStorageConfig.registerInClient(container);
        modEventBus.addListener(this::registerScreens);
        modEventBus.addListener(this::registerHelper);
        NeoForge.EVENT_BUS.addListener(SmarterContraptionStorageClient::onClientPlayerLoggedIn);
        modEventBus.addListener(Overlay::setValue);
        PonderIndex.addPlugin(new SCS_Ponder());
    }

    private static void onClientPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
        ModList list = ModList.get();
        if(list.isLoaded(FunctionalStorage) && !SmarterContraptionStorage.isFunctionalStorageLoaded(list)){
            event.getPlayer().sendSystemMessage(Component.translatable(SmarterContraptionStorage.MODID + ".warn.functional_storage",list.getModContainerById(FunctionalStorage).get().getModInfo().getVersion().toString(),FunctionalStorageMAXVersion.toString())
                    .withColor(Color.YELLOW.getRGB()));
        }
    }

    private void registerScreens(RegisterMenuScreensEvent event){
        ModList list = ModList.get();
        if(list.isLoaded("create")){
            event.register(MovingBlockEntityMenu.BlockEntityMenu.get(), MovingBlockEntityScreen::new);
            if(list.isLoaded(TrashCans)) {
                event.register(TrashHandlerHelper.TrashHandler.TrashCanMenu.get(), MovingTrashCanScreen::new);
            }
            if(list.isLoaded(StorageDrawers)){
                event.register(DrawersHandlerHelper.NormalDrawerHandler.DrawerMenu.get(), MovingDrawerScreen::new);
                event.register(CompactingHandlerHelper.CompactingHandler.CompactingDrawerMenu.get(), MovingCompactingDrawerScreen::new);
            }
            if(isFunctionalStorageLoaded(list)){
                event.register(FunctionalDrawersHandlerHelper.FDrawersHandler.MENU_TYPE.get(), MovingFunctionalDrawerScreen::new);
                event.register(FunctionalCompactingHandlerHelper.FCDrawersHandler.MENU_TYPE.get(), MovingFunctionalCompactingScreen::new);
            }
        }
    }

    private void registerHelper(FMLClientSetupEvent event){
        ModList list = ModList.get();
        if(list.isLoaded("create")){
//            StorageHandlerHelper.register(ToolboxHandlerHelper.INSTANCE);
            if(list.isLoaded(TrashCans)) {
                StorageHandlerHelper.register(new TrashHandlerHelper());
                register(new TrashcanFluidHelper());
            }
            if(list.isLoaded(StorageDrawers)) {
                StorageHandlerHelper.register(new DrawersHandlerHelper());
                StorageHandlerHelper.register(new CompactingHandlerHelper());
            }
            if(SmarterContraptionStorageConfig.AE2Loaded()) {
                StorageHandlerHelper.register(new AE2BusBlockHelper());
                StorageHandlerHelper.register(new MEStorageFilter());
                StorageHandlerHelper.register(new AEControllerBlock());
                StorageHandlerHelper.register(new AEEnergyBlock());
                StorageHandlerHelper.register(new SpatialHandler());
            }
            if(list.isLoaded(AE2))
                FluidHandlerHelper.register(new SkyStoneTankHelper());
            if(SmarterContraptionStorageConfig.RSLoaded()){
                StorageHandlerHelper.register(new RSCableHandlerHelper());
                FluidHandlerHelper.register(new RSCableFluidHelper());
                StorageHandlerHelper.register(new RSControllerBlock());
                StorageHandlerHelper.register(new InterfaceHelper());
            }
//            if(list.isLoaded(CobbleForDays))
//                StorageHandlerHelper.register(new CobblestoneGenerator());
//            if(list.isLoaded(SBackPack)){
//                StorageHandlerHelper.register(SBackPacksHandlerHelper.INSTANCE);
//                register(new SBackPacksFluidHandlerHelper());
//            }
            if(SmarterContraptionStorage.isFunctionalStorageLoaded(list)){
                StorageHandlerHelper.register(new FunctionalDrawersHandlerHelper());
                StorageHandlerHelper.register(new FunctionalCompactingHandlerHelper());
                register(new FunctionalFluidHandlerHelper());
            }
        }
    }
}