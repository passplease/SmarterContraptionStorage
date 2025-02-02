package net.smartercontraptionstorage.Mixin.Contraption;

import com.simibubi.create.content.contraptions.*;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.smartercontraptionstorage.AddStorage.FluidHander.DumpHandler;
import net.smartercontraptionstorage.AddStorage.FluidHander.FluidHandlerHelper;
import net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper;
import net.smartercontraptionstorage.AddStorage.NeedDealWith;
import net.smartercontraptionstorage.ForFunctionChanger;
import net.smartercontraptionstorage.FunctionChanger;
import net.smartercontraptionstorage.Interface.Changeable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(MountedStorageManager.class)
public abstract class MountedStorageManagerMixin implements Changeable {
    @Shadow(remap = false) protected Map<BlockPos, MountedStorage> storage;

    @Shadow(remap = false) protected Map<BlockPos, MountedFluidStorage> fluidStorage;

    @Shadow(remap = false) protected CombinedTankWrapper fluidInventory;

    @Shadow(remap = false) public abstract void attachExternal(IItemHandlerModifiable externalStorage);

    @Shadow(remap = false) protected Contraption.ContraptionInvWrapper inventory;

    @Shadow(remap = false) protected abstract Contraption.ContraptionInvWrapper wrapItems(Collection<IItemHandlerModifiable> list, boolean fuel);

    @Shadow(remap = false) protected abstract CombinedTankWrapper wrapFluids(Collection<IFluidHandler> list);

    @Shadow(remap = false) protected Contraption.ContraptionInvWrapper fuelInventory;
    @Unique public DumpHandler smarterContraptionStorage$handler;
    @Inject(method = "removeStorageFromWorld",at = @At("RETURN"),remap = false)
    public void removeStorageFromWorld_head(CallbackInfo ci){
        for(MountedStorage mountedStorage : this.storage.values())
            if(mountedStorage.getItemHandler() instanceof NeedDealWith handler)
                handler.finallyDo();
        for(MountedFluidStorage mountedFluidStorage : this.fluidStorage.values())
            if(mountedFluidStorage.getFluidHandler() instanceof NeedDealWith handler)
                handler.finallyDo();
        StorageHandlerHelper.clearData();
        FluidHandlerHelper.clearData();
    }
    @Unique
    public Collection<IItemHandlerModifiable> smarterContraptionStorage$addDumpFillingHandler(Collection<IItemHandlerModifiable> list){
        if(DumpHandler.isOpened()){
            Collection<IItemHandlerModifiable> List = new ArrayList<>();
            List.add(smarterContraptionStorage$handler);// ensure DumpHandler is the first
            List.addAll(list);
            return List;
        }
        return list;
    }
    /**
     * @author passplease
     * @reason to adjust the order of createHandler (first create fluidInventory) so that I can initialize dumpHandler
     */
    @Overwrite(remap = false)
    public void createHandlers(){
        this.fluidInventory = this.wrapFluids(this.fluidStorage.values().stream().map(MountedFluidStorage::getFluidHandler).collect(Collectors.toList()));
        smarterContraptionStorage$handler = new DumpHandler(fluidInventory);
        Collection<MountedStorage> itemHandlers = this.storage.values();
        this.inventory = this.wrapItems(smarterContraptionStorage$addDumpFillingHandler(itemHandlers.stream().map(MountedStorage::getItemHandler).toList()), false);
        this.fuelInventory = this.wrapItems(smarterContraptionStorage$addDumpFillingHandler(itemHandlers.stream().filter(MountedStorage::canUseForFuel).map(MountedStorage::getItemHandler).toList()), true);
    }
    @ForFunctionChanger(method = "deserialize")
    @Inject(method = "read",at = @At("HEAD"),remap = false)
    public void help_deserialize(CompoundTag nbt, Map<BlockPos, BlockEntity> presentBlockEntities, boolean clientPacket, CallbackInfo ci){
        FunctionChanger.presentBlockEntities = presentBlockEntities;
    }
    @ForFunctionChanger(method = "deserialize")
    @Inject(method = "read",at = @At("RETURN"),remap = false)
    public void clearData(CompoundTag nbt, Map<BlockPos, BlockEntity> presentBlockEntities, boolean clientPacket, CallbackInfo ci){
        FunctionChanger.presentBlockEntities = null;
    }

    @Override
    public void set(Object object) {}

    @Override
    public void set(String parameterName, Object object) {
        if(parameterName.equals("storage")) {
            this.storage = (Map<BlockPos, MountedStorage>) object;
        } else if(parameterName.equals("add storage")){
            Map<BlockPos, MountedStorage> map = (Map<BlockPos, MountedStorage>) object;
            map.putAll(this.storage);
            this.storage = map;
        }
    }

    @Nullable
    @Override
    public Object get(String name) {
        if(Objects.equals(name, "storage"))
            return this.storage;
        return null;
    }
}
