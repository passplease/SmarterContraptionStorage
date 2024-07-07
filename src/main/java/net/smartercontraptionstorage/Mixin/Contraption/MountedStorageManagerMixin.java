package net.smartercontraptionstorage.Mixin.Contraption;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorage;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.smartercontraptionstorage.FunctionChanger;
import net.smartercontraptionstorage.MathMethod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

import static net.smartercontraptionstorage.MathMethod.playerInteracting;

@Mixin(MountedStorageManager.class)
public abstract class MountedStorageManagerMixin {
    @Shadow(remap = false) protected Map<BlockPos, MountedStorage> storage;

    @Inject(method = "handlePlayerStorageInteraction",at = @At("HEAD"),remap = false)
    public void handlePlayerStorageInteraction_head(Contraption contraption, Player player, BlockPos localPos, CallbackInfoReturnable<Boolean> cir){
        playerInteracting = true;
    }
    @Inject(method = "handlePlayerStorageInteraction",at = @At("RETURN"),remap = false)
    public void handlePlayerStorageInteraction_bottom(Contraption contraption, Player player, BlockPos localPos, CallbackInfoReturnable<Boolean> cir){
        playerInteracting = false;
    }
    @Inject(method = "removeStorageFromWorld",at = @At("HEAD"),remap = false)
    public void removeStorageFromWorld_head(CallbackInfo ci){
        for(MountedStorage mountedStorage : this.storage.values()) {
            BlockEntity entity = FunctionChanger.findMountedEntity(mountedStorage);
            if(entity instanceof ToolboxBlockEntity){
                MathMethod.addInventory((ToolboxBlockEntity) entity);
            }
        }
    }
}
