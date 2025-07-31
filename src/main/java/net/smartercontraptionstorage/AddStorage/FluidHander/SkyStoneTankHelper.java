package net.smartercontraptionstorage.AddStorage.FluidHander;

import appeng.blockentity.storage.SkyStoneTankBlockEntity;
import appeng.core.definitions.AEBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SkyStoneTankHelper extends FluidHandlerHelper{
    @Override
    public void addStorageToWorld(BlockEntity entity, IFluidHandler tank) {
        assert canCreateHandler(entity) && entity.getLevel() != null;
        ((SkyStoneTankBlockEntity)entity).loadTag(serializeNBT(tank));
    }

    @Override
    public boolean canCreateHandler(Item comparedItem) {
        return comparedItem == AEBlocks.SKY_STONE_TANK.asItem();
    }

    @Override
    public boolean canCreateHandler(Block block) {
        return block == AEBlocks.SKY_STONE_TANK.block();
    }

    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof SkyStoneTankBlockEntity;
    }

    @Override
    public @NotNull FluidTank createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        return (FluidTank) ((SkyStoneTankBlockEntity)entity).getStorage();
    }

    @Override
    public @NotNull CompoundTag serializeNBT(IFluidHandler handler) {
        return ((FluidTank)handler).writeToNBT(new CompoundTag());
    }

    @Override
    public String getName() {
        return "SkyStoneTank";
    }

    @Override
    public @NotNull IFluidHandler deserialize(CompoundTag nbt, HolderLookup.Provider provider) {
        FluidTank tank = new FluidTank(SkyStoneTankBlockEntity.BUCKET_CAPACITY * 1000);
        tank.readFromNBT(nbt);
        return tank;
    }

    @Override
    public void registerBlock(Consumer<Block> register) {
        register.accept(AEBlocks.SKY_STONE_TANK.block());
    }
}
