package net.smartercontraptionstorage.AddStorage.ItemHandler;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.BlockCompDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawersComp;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class CompactingHandlerHelper extends DrawersHandlerHelper{
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof BlockEntityDrawersComp;
    }
    @Override
    public @NotNull ItemStackHandler createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        IDrawerGroup group = ((BlockEntityDrawersComp) entity).getGroup();
        return new CompactingHandler(group);
    }
    @Override
    public boolean allowControl(Block block) {
        return block instanceof BlockCompDrawers;
    }
    @Override
    public String getName() {
        return "CompactingHandlerHelper";
    }
    protected static class CompactingHandler extends NormalDrawerHandler{
        private static final ItemStack placeHolder = Items.STONE.getDefaultInstance();
        public CompactingHandler(IDrawerGroup group) {
            super(group);
            // below is to deal with empty comDrawers, aiming to get right limits (always get 0 without it)
            if(group.getDrawer(0).isEmpty()) {
                for (int i = group.getDrawerCount() - 1; i >= 0; i--) {
                    if(group.getDrawer(i).getStoredItemPrototype().isEmpty())
                        group.getDrawer(i).setStoredItem(placeHolder);
                    group.getDrawer(i).setStoredItemCount(1);
                    if(group.getDrawer(i).getAcceptingRemainingCapacity() == Integer.MAX_VALUE)
                        slotLimits[i] = Integer.MAX_VALUE;
                    else slotLimits[i] = group.getDrawer(i).getMaxCapacity();
                }
            }
        }
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(slot != 0)// only allow to extract the first slot (which is always blockItem)
                return ItemStack.EMPTY;
            else return super.extractItem(slot,amount,simulate);
        }
    }
}