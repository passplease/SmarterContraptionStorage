package net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper;

import com.refinedmods.refinedstorage.common.content.Blocks;
import com.refinedmods.refinedstorage.common.iface.InterfaceBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.smartercontraptionstorage.Mixin.RS.AbstractBaseNetworkNodeContainerBlockEntityMixin;

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
        register.accept(Blocks.INSTANCE.getInterface());
    }
}
