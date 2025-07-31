package net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.blockentity.InterfaceBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Consumer;

public class InterfaceHelper extends InitializeHelper{
    @Override
    public boolean canDoSomething(BlockEntity entity) {
        return entity instanceof InterfaceBlockEntity;
    }

    @Override
    public void doSomething(BlockEntity entity) {
        normallyDo(entity);
    }

    @Override
    public void registerBlock(Consumer<Block> register) {
        register.accept(RSBlocks.INTERFACE.get());
    }
}
