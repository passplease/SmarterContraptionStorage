package net.smartercontraptionstorage.Render;

import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.smartercontraptionstorage.SmarterContraptionStorage;

@Mod.EventBusSubscriber(modid = SmarterContraptionStorage.MODID,value = {Dist.CLIENT}, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Textures {
    // register by json file
//    @SubscribeEvent
//    public static void register(TextureStitchEvent.Pre event){
//        if(event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS)){
//            Overlay.register(event);
//        }
//    }
    @SubscribeEvent
    public static void setValue(TextureStitchEvent.Post event){
        if(event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS)){
            Overlay.setUV(event);
        }
    }
}
