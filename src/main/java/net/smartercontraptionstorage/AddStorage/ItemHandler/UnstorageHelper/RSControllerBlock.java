package net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.blockentity.ControllerBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

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
        RSBlocks.CONTROLLER.values().forEach(b -> b.ifPresent(register));
        RSBlocks.CREATIVE_CONTROLLER.values().forEach(b -> b.ifPresent(register));
    }
}
