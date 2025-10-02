package net.smartercontraptionstorage.AddStorage.ItemHandler;

import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import appeng.blockentity.networking.*;
import appeng.core.definitions.AEBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;

public class AE2BusBlockHelper extends StorageHandlerHelper{
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof CableBusBlockEntity;
    }

    @Override
    public void addStorageToWorld(BlockEntity entity, ItemStackHandler handler) {}

    @Override
    public @NotNull ItemStackHandler createHandler(BlockEntity entity) {
        assert this.canCreateHandler(entity);
        return AE2BusHelper.createHandler(entity,NULL_HANDLER,AE2HandlerHelper::new);
    }

    @Override
    public boolean allowControl(Item comparedItem) {
        return false;
    }

    @Override
    public boolean allowControl(Block block) {
        return false;
    }

    @Override
    public void registerBlock(Consumer<Block> register) {
        AE2BusHelper.registerBlock(register);
    }

    @Override
    public String getName() {
        return "AE2BusBlockHelper";
    }

    @Override
    public boolean canDeserialize() {
        return false;
    }

    @Override
    public ItemStackHandler deserialize(CompoundTag nbt, HolderLookup.Provider provider, boolean client) throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static class AE2HandlerHelper extends ItemStackHandler implements AE2BusHelper {
        protected final @Nullable IGridNode extractNode;
        protected final @Nullable IGridNode importNode;
        private final ArrayList<AEKey> extractKeys = new ArrayList<>();
        private boolean hasFilter = false;
        private boolean canWork = false;
        private final AE2ContraptionSource extractSource;
        private final AE2ContraptionSource importSource;
        protected AE2HandlerHelper(int size, @Nullable IGridNode extractNode, @Nullable IGridNode importNode) {
            super(size);
            this.extractNode = extractNode;
            this.importNode = importNode;
            this.extractSource = AE2ContraptionSource.create(extractNode);
            this.importSource = AE2ContraptionSource.create(importNode);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if(slot == 0)
                stack.shrink(Math.toIntExact(AE2BusHelper.super.insert(AEItemKey.of(stack),stack.getCount(), Actionable.ofSimulate(simulate))));
            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(validSlot(slot)) {
                long extracted = AE2BusHelper.super.extract(extractKeys.get(slot), amount, Actionable.ofSimulate(simulate));
                return extracted <= 0 ? ItemStack.EMPTY : extractKeys.get(slot).wrapForDisplayOrFilter().copyWithCount((int) extracted);
            }else return ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            if(canWork(true,true) && validSlot(slot) && !onlyInsert()) {
                MEStorage extractStorage = getStorage(true);
                if (extractStorage == null)
                    return ItemStack.EMPTY;
                refreshStack(extractStorage);
                ItemStack stack = extractKeys.get(slot).wrapForDisplayOrFilter();
                int size = (int) extractStorage.extract(extractKeys.get(slot), Integer.MAX_VALUE, Actionable.SIMULATE, extractSource);
                stack.setCount(size);
                return stack;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public boolean isItemHandler() {
            return true;
        }

        @Override
        public boolean isFluidHandler() {
            return false;
        }

        @Override
        public @NotNull CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            return AE2BusHelper.super.serializeNBT(provider,super.serializeNBT(provider));
        }

        @Override
        public boolean getCanWork() {
            if(getExtractNode() == null && getImportNode() == null)
                canWork = false;
            return canWork;
        }

        @Override
        public void setCanWork(boolean canWork) {
            if(getExtractNode() == null && getImportNode() == null)
                canWork = false;
            this.canWork = canWork;
        }

        @Override
        public boolean hasFilter() {
            return hasFilter;
        }

        @Override
        public void setFilter(boolean hasFilter) {
            this.hasFilter = hasFilter;
        }

        @Override
        public @Nullable IGridNode getExtractNode() {
            return extractNode != null && extractNode.isActive() ? extractNode : null;
        }

        @Override
        public @Nullable IGridNode getImportNode() {
            return importNode != null && importNode.isActive() ? importNode : null;
        }

        @Override
        public ArrayList<AEKey> getExtractKeys() {
            return extractKeys;
        }

        @Override
        public AE2ContraptionSource getExtractSource() {
            return extractSource;
        }

        @Override
        public AE2ContraptionSource getImportSource() {
            return importSource;
        }
    }
}