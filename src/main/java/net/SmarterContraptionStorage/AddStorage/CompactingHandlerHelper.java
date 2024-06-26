package net.SmarterContraptionStorage.AddStorage;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawersComp;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class CompactingHandlerHelper extends DrawersHandlerHelper{
    @Override
    protected boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof BlockEntityDrawersComp;
    }
    @Override
    public ItemStackHandler createHandler(BlockEntity entity) {
        IDrawerGroup group = ((BlockEntityDrawersComp) entity).getGroup();
        return new CompactingHandler(group);
    }
    @Override
    public boolean allowControl(Block block) {
        return block instanceof BlockCompDrawers;
    }
    protected static class CompactingHandler extends NormalDrawerHandler{
        public CompactingHandler(IDrawerGroup group) {
            super(group);
        }
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(slot != 0)
                return ItemStack.EMPTY;
            else return super.extractItem(slot,amount,simulate);
        }
    }
}