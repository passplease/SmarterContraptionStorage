package net.smartercontraptionstorage;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.smartercontraptionstorage.Render.Overlay;

@Mod.EventBusSubscriber(modid = SmarterContraptionStorage.MODID)
public class Command {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        Overlay.registerCommand(dispatcher,event.getBuildContext());
    }
}
