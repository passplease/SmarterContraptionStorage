package net.SmarterContraptionStorage.Mixin.Contraption;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.SmarterContraptionStorage.MathMethod;
import net.SmarterContraptionStorage.SmarterContraptionStorageConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Queue;
import java.util.Set;

import static net.SmarterContraptionStorage.MathMethod.*;
@Mixin(Contraption.class)
public abstract class ContraptionMixin {
    @Shadow(remap = false) protected abstract void addBlock(BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair);
    @Unique private boolean smarterContraptionStorage$waitAddBlock = true;
    // To know when it will return ("searchMovedStructure" method) and my "addBlock" method work for once
    @Unique private final HashMap<BlockPos,Boolean> smarterContraptionStorage$checkedBlockPos = new HashMap<>();
    @Redirect(method = "moveBlock",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/Contraption;addBlock(Lnet/minecraft/core/BlockPos;Lorg/apache/commons/lang3/tuple/Pair;)V"),remap = false)
    public void storeBlock(Contraption instance, BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair){
        if(canBeControlledBlock(pair.getRight().getBlockState().getBlock().asItem())){
            addData(MathMethod.pos,pos);
            addData(MathMethod.pair,pair);
        }else this.addBlock(pos,pair);
    }
    @Inject(method = "moveBlock",at = @At("HEAD"),remap = false)
    public void setNeedAddBlock(Level world, Direction forcedDirection, Queue<BlockPos> frontier, Set<BlockPos> visited, CallbackInfoReturnable<Boolean> cir){
        smarterContraptionStorage$waitAddBlock = false;
    }
    @Inject(method = "searchMovedStructure",at = @At("RETURN"),remap = false)
    // To add storage block after "searchMovedStructure" (use Excludes.a variant to do this)

    public void addBlock(Level world, BlockPos pos, Direction forcedDirection, CallbackInfoReturnable<Boolean> cir) throws AssemblyException {
        if(smarterContraptionStorage$waitAddBlock)
            return;
        if (MathMethod.pos.isEmpty() && pair.isEmpty())
            return;
        else if (pair.size() != pair.size())
            throw new AssemblyException("The block status is broken or doesn't have block status at all!", new Exception());
        Block thisBlock;
        for (int i = 0; i < MathMethod.pos.size(); i++) {
            thisBlock = pair.get(i).getRight().getBlockState().getBlock();
            pos = MathMethod.pos.get(i);
            if (canBeControlledBlock(thisBlock.asItem()))
                smarterContraptionStorage$canStoreItem = !SmarterContraptionStorage$checkAddToStorage(world,pos,thisBlock, SmarterContraptionStorageConfig.CheckAdjacentBlock.get());
            smarterContraptionStorage$checkedBlockPos.put(pos, smarterContraptionStorage$canStoreItem);
            this.addBlock(pos,pair.get(i));
        }
        smarterContraptionStorage$waitAddBlock = true;
        smarterContraptionStorage$checkedBlockPos.clear();
        resetStatus();
    }
    @Unique
    public boolean SmarterContraptionStorage$checkAddToStorage(Level world, BlockPos thisPos, Block thisBlock, boolean search){
        Boolean checked = smarterContraptionStorage$checkedBlockPos.get(thisPos);
        if(checked != null)
            return checked;
        for (BlockPos block : getAroundedBlockPos(thisPos)) {
            BlockEntity Block = world.getBlockEntity(block);
            if (Block instanceof ContraptionControlsBlockEntity &&
                    ((ContraptionControlsBlockEntity) Block).filtering.getFilter().getItem() == thisBlock.asItem()) {
                return SmarterContraptionStorageConfig.getDefaultOpen(((ContraptionControlsBlockEntity) Block).disabled);
            }
            if (search && Block != null &&  thisBlock == Block.getBlockState().getBlock()) {
                return searchBlockPos(world,thisPos,
                        (level,pos,initialBlock) -> thisBlock == level.getBlockState(pos).getBlock(),
                        (level,pos,initialBlock,nowReturnValue,t) -> {
                            if (t == null)
                                t = false;
                            if (nowReturnValue == null)
                                nowReturnValue = false;
                            return nowReturnValue || t || SmarterContraptionStorage$checkAddToStorage(level, pos, thisBlock, false);
                        },
                        (level, pos, blockState,returnValue)-> smarterContraptionStorage$checkedBlockPos.put(pos, returnValue));
            }
        }
        return SmarterContraptionStorageConfig.getDefaultOpen(false);
        // Make sure if player set false I can close storage block without ContraptionControlsBlock
    }
}