package net.smartercontraptionstorage.Mixin.Contraption;

import com.simibubi.create.content.contraptions.*;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.network.NetworkHooks;
import net.smartercontraptionstorage.AddStorage.FluidHander.DumpHandler;
import net.smartercontraptionstorage.AddStorage.FluidHander.FluidHandlerHelper;
import net.smartercontraptionstorage.AddStorage.GUI.BlockEntityMenu.HelperMenuProvider;
import net.smartercontraptionstorage.AddStorage.GUI.ContraptionMenuProvider;
import net.smartercontraptionstorage.AddStorage.GUI.NormalMenu.MovingMenuProvider;
import net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper;
import net.smartercontraptionstorage.AddStorage.NeedDealWith;
import net.smartercontraptionstorage.Interface.Changeable;
import net.smartercontraptionstorage.Interface.Gettable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(MountedStorageManager.class)
public abstract class MountedStorageManagerMixin implements Changeable {
    @Shadow(remap = false) protected Map<BlockPos, MountedStorage> storage;

    @Shadow(remap = false) protected Map<BlockPos, MountedFluidStorage> fluidStorage;

    @Shadow(remap = false) protected CombinedTankWrapper fluidInventory;

    @Shadow(remap = false) protected Contraption.ContraptionInvWrapper inventory;

    @Shadow(remap = false) protected abstract Contraption.ContraptionInvWrapper wrapItems(Collection<IItemHandlerModifiable> list, boolean fuel);

    @Shadow(remap = false) protected abstract CombinedTankWrapper wrapFluids(Collection<IFluidHandler> list);

    @Shadow(remap = false) protected Contraption.ContraptionInvWrapper fuelInventory;
    @Unique public DumpHandler smarterContraptionStorage$handler;
    @Inject(method = "removeStorageFromWorld",at = @At("RETURN"),remap = false)
    public void removeStorageFromWorld(CallbackInfo ci){
        this.storage.forEach((pos, mountedStorage) -> {
            if(mountedStorage.getItemHandler() instanceof NeedDealWith handler)
                handler.finallyDo();
            else if(((Gettable)mountedStorage).get("helper") instanceof NeedDealWith helper)
                helper.finallyDo();
        });
        this.fluidStorage.forEach((pos, mountedFluidStorage) -> {
            if(mountedFluidStorage.getFluidHandler() instanceof NeedDealWith handler)
                handler.finallyDo();
            else if(((Gettable)mountedFluidStorage).get("helper") instanceof NeedDealWith helper)
                helper.finallyDo();
        });
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

    @Inject(method = "write",at = @At(value = "RETURN"),remap = false)
    public void write(CompoundTag nbt, boolean clientPacket, CallbackInfo ci){
        ListTag storageNbt = (ListTag)nbt.get("Storage");
        storageNbt.forEach((tag) -> {
            CompoundTag storageTag = (CompoundTag)tag;
            storageTag.getCompound("Data").put("LocalPos", storageTag.getCompound("Pos"));
        });
        ListTag fluidStorageNbt = (ListTag)nbt.get("FluidStorage");
        fluidStorageNbt.forEach((tag) -> {
            CompoundTag storageTag = (CompoundTag)tag;
            storageTag.getCompound("Data").put("LocalPos", storageTag.getCompound("Pos"));
        });
    }

    @Inject(method = "handlePlayerStorageInteraction",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/Contraption;getStorageForSpawnPacket()Lcom/simibubi/create/content/contraptions/MountedStorageManager;",shift = At.Shift.AFTER,by = -1),remap = false,cancellable = true)
    public void handlePlayerStorageInteraction(Contraption contraption, Player player, BlockPos localPos, CallbackInfoReturnable<Boolean> cir){
        MountedStorage storage = ((Map<BlockPos, MountedStorage>) ((Gettable) ((Gettable) contraption).get("manager")).get("storage")).get(localPos);
        if(storage != null) {
            ContraptionMenuProvider<?> provider = null;
            if(storage.getItemHandler() instanceof MovingMenuProvider h && !h.hasOpened())
                provider = h;
            else {
                Object helper = ((Gettable) storage).get("helper");
                if(helper instanceof HelperMenuProvider<?>) {
                    HelperMenuProvider<?> h = ((HelperMenuProvider<?>) helper).get();
                    BlockEntity blockEntity = (BlockEntity) ((Gettable) storage).get("blockEntity");
                    if(h.canOpenMenu(blockEntity) && !h.hasOpened()) {
                        h.setBlockEntity(blockEntity);
                        provider = h;
                    }
                }
            }
            if(provider != null){
                provider.setContraption(contraption.entity);
                provider.setLocalPos(localPos);
                if(provider.check()){
                    provider.rememberPlayer((ServerPlayer) player);
                    NetworkHooks.openScreen((ServerPlayer) player,provider,provider::writeToBuffer);
                    provider.playSound(player.level);
                    cir.setReturnValue(true);
                }else provider.error();
            }
        }
    }

    @Override
    public void set(Object object) {}

    @Override
    public void set(String parameterName, Object object) {
        if(parameterName.equals("storage") && object instanceof Map) {
            this.storage = (Map<BlockPos, MountedStorage>) object;
        }
    }

    @Nullable
    @Override
    public Object get(String name) {
        return switch (name){
            case "storage" -> storage;
            case "fluidStorage" -> fluidStorage;
            case "inventory" -> inventory;
            case "fluidInventory" -> fluidInventory;
            case "fuelInventory" -> fuelInventory;
            default -> null;
        };
    }
}
