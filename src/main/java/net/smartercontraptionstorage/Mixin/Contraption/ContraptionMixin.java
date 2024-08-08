package net.smartercontraptionstorage.Mixin.Contraption;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.smartercontraptionstorage.ForFunctionChanger;
import net.smartercontraptionstorage.FunctionChanger;
import net.smartercontraptionstorage.Utils;
import net.smartercontraptionstorage.SmarterContraptionStorageConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static net.smartercontraptionstorage.Utils.*;
@Mixin(Contraption.class)
public abstract class ContraptionMixin {
    @Shadow(remap = false) protected abstract void addBlock(BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair);

    @Shadow(remap = false) protected MountedStorageManager storage;
    @Unique private boolean smarterContraptionStorage$waitAddBlock = true;
    // To know when it will return ("searchMovedStructure" method) and my "addBlock" method work for once
    @Unique private final HashMap<BlockPos,Boolean> smarterContraptionStorage$checkedBlockPos = new HashMap<>();
    @Redirect(method = "moveBlock",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/Contraption;addBlock(Lnet/minecraft/core/BlockPos;Lorg/apache/commons/lang3/tuple/Pair;)V"),remap = false)
    public void storeBlock(Contraption instance, BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair){
        BlockEntity entity = pair.getRight();
        if(entity != null) {
            if (canBeControlledBlock(entity.getBlockState().getBlock())) {
                addData(Utils.pos, pos);
                addData(Utils.pair, pair);
                return;
            }
        }
        addBlock(pos,pair);
    }
    @Inject(method = "moveBlock",at = @At("HEAD"),remap = false)
    public void setNeedAddBlock(Level world, Direction forcedDirection, Queue<BlockPos> frontier, Set<BlockPos> visited, CallbackInfoReturnable<Boolean> cir){
        smarterContraptionStorage$waitAddBlock = false;
    }
    @Inject(method = "searchMovedStructure",at = @At("RETURN"),remap = false)
    // To add storage block after "searchMovedStructure" (use Excludes.SpatialPylonBlockEntityMixin variant to do this)
    public void addBlock(Level world, BlockPos pos, Direction forcedDirection, CallbackInfoReturnable<Boolean> cir) {
        if(smarterContraptionStorage$waitAddBlock)
            return;
        if (Utils.pos.isEmpty() && pair.isEmpty())
            return;
        else if (Utils.pos.size() != pair.size())
            throw new IllegalCallerException("The block data is broken or doesn't have block status at all!", new Exception());
        Block thisBlock;
        for (int i = 0; i < Utils.pos.size(); i++) {
            thisBlock = pair.get(i).getRight().getBlockState().getBlock();
            pos = Utils.pos.get(i);
            if (canBeControlledBlock(thisBlock))
                smarterContraptionStorage$canUseForStorage = !SmarterContraptionStorage$checkAddToStorage(world,pos,thisBlock, SmarterContraptionStorageConfig.CHECK_ADJACENT_BLOCK.get());
            smarterContraptionStorage$checkedBlockPos.put(pos, smarterContraptionStorage$canUseForStorage);
            this.addBlock(pos,pair.get(i));
        }
        smarterContraptionStorage$waitAddBlock = true;
        smarterContraptionStorage$checkedBlockPos.clear();
        resetData();
    }
    @Unique
    public boolean SmarterContraptionStorage$checkAddToStorage(Level world, BlockPos thisPos, Block thisBlock, boolean search){
        Boolean checked = smarterContraptionStorage$checkedBlockPos.get(thisPos);
        if(checked != null)
            return checked;
        for (BlockPos block : getAroundedBlockPos(thisPos)) {
            BlockEntity Block = world.getBlockEntity(block);
            if (Block instanceof ContraptionControlsBlockEntity CB &&
                    CB.filtering.getFilter().getItem() == thisBlock.asItem()) {
                return SmarterContraptionStorageConfig.getDefaultOpen(CB.disabled);
            }
            if (search && Block != null && thisBlock == Block.getBlockState().getBlock()) {
                return searchBlockPos(world,thisPos,
                        (level,pos,initialBlock) -> thisBlock == level.getBlockState(pos).getBlock(),
                        (level,pos,initialBlock,nowReturnValue,t) -> {
                            if (t == null)
                                t = false;
                            return nowReturnValue || t || level.getBlockState(pos).getBlock() == thisBlock && SmarterContraptionStorage$checkAddToStorage(level, pos, thisBlock, false);
                        },
                        (level, pos, blockState,returnValue) -> smarterContraptionStorage$checkedBlockPos.put(pos, returnValue),false);
            }
        }
        return !SmarterContraptionStorageConfig.DEFAULT_OPEN.get();
        // Make sure if player set false I can close storage block without ContraptionControlsBlock
    }
    @Deprecated
    @ForFunctionChanger(method = "openGUI")
    @Inject(method = "isHiddenInPortal",at = @At("HEAD"),cancellable = true,remap = false)
    public void getStorageForSpawnPacket(BlockPos localPos, CallbackInfoReturnable<Boolean> cir){
        if(FunctionChanger.isOpenGUI()){
            FunctionChanger.setStorage(storage);
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}