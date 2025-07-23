package net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper;

import com.refinedmods.refinedstorage.common.content.Blocks;
import com.refinedmods.refinedstorage.common.controller.ControllerBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.smartercontraptionstorage.Mixin.RS.AbstractBaseNetworkNodeContainerBlockEntityMixin;

import java.util.function.Consumer;

public class RSControllerBlock extends InitializeHelper{
    @Override
    public boolean canDoSomething(BlockEntity entity) {
        return entity instanceof ControllerBlockEntity;
    }

    @Override
    public void doSomething(BlockEntity entity) {
        normallyDo(entity);
    }

    @Override
    public void registerBlock(Consumer<Block> register) {
        Blocks.INSTANCE.getController().values().forEach(register);
        Blocks.INSTANCE.getCreativeController().values().forEach(register);
    }
}
