package net.smartercontraptionstorage;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.smartercontraptionstorage.Render.Overlay;

public class Command {
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        Overlay.registerCommand(dispatcher,event.getBuildContext());
    }
}
