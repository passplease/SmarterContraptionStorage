package net.SmarterContraptionStorage.AddStorage;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.capabilities.DrawerItemHandler;
import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;
import com.simibubi.create.content.contraptions.MountedStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class DrawersHandlerHelper extends StorageHandlerHelper {
    @Override
    protected boolean canCreateHandler(BlockEntity Entity) {
        return Entity instanceof BlockEntityDrawers;
    }
    @Override
    public void addStorageToWorld(BlockEntity entity,ItemStackHandler handler) {
        IDrawerGroup group = ((BlockEntityDrawers) entity).getGroup();
        for(int i = handler.getSlots() - 1;i >= 0;i--){
            group.getDrawer(i).setStoredItem(handler.getStackInSlot(i),handler.getStackInSlot(i).getCount());
        }
    }
    @Override
    public ItemStackHandler createHandler(BlockEntity entity) {
        IDrawerGroup group = ((BlockEntityDrawers) entity).getGroup();
        return new DrawerHandler(group);
    }
    @Override
    public boolean allowControl(Item comparedItem) {
        return comparedItem instanceof ItemDrawers;
    }
    private static class DrawerHandler extends ItemStackHandler{
        private final DrawerItemHandler drawer;
        public DrawerHandler(IDrawerGroup group) {
            super(group.getDrawerCount());
            drawer = new DrawerItemHandler(group);
        }
        @Override
        public int getSlotLimit(int slot) {
            return getDrawer().getSlotLimit(slot);
        }
        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return getDrawer().getSlotLimit(slot);
        }
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return getDrawer().insertItem(slot,stack,simulate);
        }
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return getDrawer().extractItem(slot,amount,simulate);
        }
        @Override
        public int getSlots() {
            return getDrawer().getSlots();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return getDrawer().getStackInSlot(slot);
        }
        @Override
        public CompoundTag serializeNBT() {
            ListTag nbtTagList = new ListTag();

            for(int i = 0; i < this.stacks.size(); ++i) {
                if (!getDrawer().getStackInSlot(i).isEmpty()) {
                    CompoundTag itemTag = new CompoundTag();
                    itemTag.putInt("Slot", i);
                    getDrawer().getStackInSlot(i).save(itemTag);
                    nbtTagList.add(itemTag);
                }
            }

            CompoundTag nbt = new CompoundTag();
            nbt.put("Items", nbtTagList);
            nbt.putInt("Size", this.stacks.size());
            return nbt;
        }
        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.setSize(nbt.contains("Size", 3) ? nbt.getInt("Size") : drawer.getSlots());
            ListTag tagList = nbt.getList("Items", 10);

            for(int i = 0; i < tagList.size(); ++i) {
                CompoundTag itemTags = tagList.getCompound(i);
                int slot = itemTags.getInt("Slot");
                if (slot >= 0 && slot < drawer.getSlots()) {
                    drawer.insertItem(slot, ItemStack.of(itemTags),false);
                }
            }

            this.onLoad();
        }
        public DrawerItemHandler getDrawer() {
            return drawer;
        }
    }
}