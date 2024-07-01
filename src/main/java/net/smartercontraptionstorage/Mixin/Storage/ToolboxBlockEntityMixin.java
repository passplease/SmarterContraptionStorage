package net.smartercontraptionstorage.Mixin.Storage;

import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
@Deprecated // Unfinished now
@Mixin(ToolboxBlockEntity.class)
public abstract class ToolboxBlockEntityMixin extends SmartBlockEntity implements MenuProvider, Nameable {
    @Shadow(remap = false) ToolboxInventory inventory;

    public ToolboxBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> list) {}
    @Override
    @Shadow
    public abstract Component getName();
    @Override
    @Shadow
    public abstract Component getDisplayName();
    @Nullable
    @Override
    @Shadow(remap = false)
    public abstract AbstractContainerMenu createMenu(int i, Inventory inventory, Player player);
}
