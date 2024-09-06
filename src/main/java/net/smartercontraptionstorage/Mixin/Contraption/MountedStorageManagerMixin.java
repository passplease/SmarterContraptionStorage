package net.smartercontraptionstorage.Mixin.Contraption;

import com.simibubi.create.content.contraptions.*;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.smartercontraptionstorage.AddStorage.FluidHander.DumpHandler;
import net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper;
import net.smartercontraptionstorage.AddStorage.NeedDealWith;
import net.smartercontraptionstorage.ForFunctionChanger;
import net.smartercontraptionstorage.FunctionChanger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static net.smartercontraptionstorage.Utils.playerInteracting;

@Mixin(MountedStorageManager.class)
public abstract class MountedStorageManagerMixin {
    @Shadow(remap = false) protected Map<BlockPos, MountedStorage> storage;
    @Shadow(remap = false) protected Map<BlockPos, MountedFluidStorage> fluidStorage;

    @Shadow(remap = false) protected CombinedTankWrapper fluidInventory;

    @Shadow(remap = false) public abstract void attachExternal(IItemHandlerModifiable externalStorage);

    @Shadow(remap = false) protected Contraption.ContraptionInvWrapper inventory;

    @Shadow(remap = false) protected abstract Contraption.ContraptionInvWrapper wrapItems(Collection<IItemHandlerModifiable> list, boolean fuel);

    @Shadow(remap = false) protected abstract CombinedTankWrapper wrapFluids(Collection<IFluidHandler> list);

    @Shadow(remap = false) protected Contraption.ContraptionInvWrapper fuelInventory;
    @Unique public DumpHandler smarterContraptionStorage$handler;
    @ForFunctionChanger(method = {"net.smartercontraptionstorage.Mixin.Storage.getItemHandler","openGUI"})
    @Inject(method = "handlePlayerStorageInteraction",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/Contraption;getStorageForSpawnPacket()Lcom/simibubi/create/content/contraptions/MountedStorageManager;",by = -1),remap = false)
    public void openGUI(Contraption contraption, Player player, BlockPos localPos, CallbackInfoReturnable<Boolean> cir){
        playerInteracting = true;
//        if(FunctionChanger.openGUI(contraption,player,localPos)){
//            cir.setReturnValue(true);
//            cir.cancel();
//        }
    }
    @Deprecated
    @ForFunctionChanger(method = "openGUI")
    @Inject(method = "getItems",at = @At("HEAD"),remap = false)
    public void getMap(CallbackInfoReturnable<IItemHandlerModifiable> cir){
        if(FunctionChanger.isOpenGUI())
            FunctionChanger.setMap(storage);
    }
    @Deprecated
    @ForFunctionChanger(method = "openGUI")
    @Inject(method = "handlePlayerStorageInteraction",at = @At("RETURN"),remap = false)
    public void handlePlayerStorageInteraction_bottom(Contraption contraption, Player player, BlockPos localPos, CallbackInfoReturnable<Boolean> cir){
        playerInteracting = false;
    }
    @Inject(method = "removeStorageFromWorld",at = @At("RETURN"),remap = false)
    public void removeStorageFromWorld_head(CallbackInfo ci){
        for(MountedStorage mountedStorage : this.storage.values())
            if(mountedStorage.getItemHandler() instanceof NeedDealWith)
                ((NeedDealWith) mountedStorage.getItemHandler()).finallyDo();
        StorageHandlerHelper.clearData();
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
}
