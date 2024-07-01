package net.smartercontraptionstorage.Mixin.Storage;

import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
@Deprecated // Unfinished now
@Mixin(ToolboxInventory.class)
public abstract class ToolboxInventoryMixin extends ItemStackHandler {}