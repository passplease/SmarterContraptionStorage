package net.morestorageforcreate.Mixin;

import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.morestorageforcreate.Status;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MountedStorageManager.class)
public abstract class MountedStorageMixin{
    private static BlockPos dealBlockPos(BlockPos pos,double deltaX,double deltaY,double deltaZ){
        return new BlockPos(pos.getX() + deltaX,pos.getY() + deltaY,pos.getZ() + deltaZ);
    }
    private static BlockEntity[] getAroundBlock(Level level, BlockPos blockPos){
        BlockEntity[] aroundBlocks = new BlockEntity[6];
        aroundBlocks[0] = level.getBlockEntity(dealBlockPos(blockPos,1,0,0));
        aroundBlocks[1] = level.getBlockEntity(dealBlockPos(blockPos,-1,0,0));
        aroundBlocks[2] = level.getBlockEntity(dealBlockPos(blockPos,0,1,0));
        aroundBlocks[3] = level.getBlockEntity(dealBlockPos(blockPos,0,-1,0));
        aroundBlocks[4] = level.getBlockEntity(dealBlockPos(blockPos,0,0,1));
        aroundBlocks[5] = level.getBlockEntity(dealBlockPos(blockPos,0,0,-1));
        return aroundBlocks;
    }
    private static <T> void returnStatus(CallbackInfoReturnable<T> cir,T status){
        cir.setReturnValue(status);
        cir.cancel();
    }
}