package net.smartercontraptionstorage.Mixin.Contraption;

import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorage;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageWrapper;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.smartercontraptionstorage.AddStorage.FluidHander.DumpHandler;
import net.smartercontraptionstorage.AddStorage.FluidHander.FluidHandlerHelper;
import net.smartercontraptionstorage.AddStorage.FluidHander.MovingFluidStorage;
import net.smartercontraptionstorage.AddStorage.ItemHandler.MovingItemStorage;
import net.smartercontraptionstorage.AddStorage.ItemHandler.MovingItemStorageType;
import net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper;
import net.smartercontraptionstorage.AddStorage.ItemHandler.UnstorageHelper.InitializeHelper;
import net.smartercontraptionstorage.AddStorage.NeedDealWith;
import net.smartercontraptionstorage.Mixin.Storage.CombinedInvWrapperMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(MountedStorageManager.class)
public interface MountedStorageManagerMixin {
    @Accessor("itemsBuilder")
    void setItemsBuilder(Map<BlockPos, MountedItemStorage> itemsBuilder);

    @Accessor("itemsBuilder")
    Map<BlockPos, MountedItemStorage> getItemsBuilder();

    @Mixin(MountedStorageManager.class)
    abstract class MountedStorageManagerMixin_ {
        @Shadow(remap = false) private Map<BlockPos, MountedItemStorage> itemsBuilder;

        @Shadow(remap = false) private Map<BlockPos, MountedFluidStorage> fluidsBuilder;

        @Shadow(remap = false) protected MountedItemStorageWrapper items;

        @Shadow(remap = false) protected MountedFluidStorageWrapper fluids;

        @Shadow(remap = false) protected abstract boolean isInitialized();

        @Inject(method = "initialize",at = @At(value = "HEAD"),remap = false)
        public void removeStorageFromWorld(CallbackInfo ci){
            if(!isInitialized()) {
                List<MountedItemStorage> needDoSomething = itemsBuilder.values().stream().filter(storage -> storage instanceof MovingItemStorage).toList();
                needDoSomething.forEach(storage -> ((MovingItemStorage) storage).doSomething(itemsBuilder));
                needDoSomething.forEach(storage -> ((MovingItemStorage) storage).finallyDo(itemsBuilder));
                itemsBuilder.entrySet().removeIf(storage -> storage.getValue() instanceof MovingItemStorage && !((MovingItemStorage) storage.getValue()).canWork());
                List<MountedFluidStorage> needDoSomethingFluid = fluidsBuilder.values().stream().filter(storage -> storage instanceof MovingFluidStorage).toList();
                needDoSomethingFluid.forEach(storage -> ((MovingFluidStorage) storage).doSomething(fluidsBuilder,itemsBuilder));
                needDoSomethingFluid.forEach(storage -> ((MovingFluidStorage) storage).finallyDo(fluidsBuilder,itemsBuilder));
                fluidsBuilder.entrySet().removeIf(storage -> storage.getValue() instanceof MovingFluidStorage && !((MovingFluidStorage) storage.getValue()).canWork());
                StorageHandlerHelper.clearData();
                FluidHandlerHelper.clearData();
            }
        }

        @Inject(method = "initialize",at = @At("RETURN"),remap = false)
        public void setDumpHandler(CallbackInfo ci){
            if(DumpHandler.isOpened())
                ((CombinedInvWrapperMixin)items).getHandlers()[0] = new DumpHandler(fluids);
        }

        @Inject(method = {"lambda$read$6","lambda$read$8"},at = @At("HEAD"),remap = false)
        public void writePos(HolderLookup.Provider registries, CompoundTag tag, CallbackInfo ci){
            Optional<BlockPos> pos = NbtUtils.readBlockPos(tag, "pos");
            pos.ifPresent(blockPos -> tag.getCompound("storage").put(MovingItemStorageType.TAG, NbtUtils.writeBlockPos(blockPos)));
        }
    }
}
