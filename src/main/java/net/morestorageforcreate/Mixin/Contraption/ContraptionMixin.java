package net.morestorageforcreate.Mixin.Contraption;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.morestorageforcreate.MathMethod;
import net.morestorageforcreate.MoreContraptionStorageConfig;
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
import java.util.concurrent.atomic.AtomicBoolean;

import static net.morestorageforcreate.MathMethod.*;
@Mixin(Contraption.class)
public abstract class ContraptionMixin {
    @Shadow(remap = false) protected abstract void addBlock(BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair);
    @Unique private boolean moreStorageForCreate$waitAddBlock = true;
    // To know when it will return ("searchMovedStructure" method) and my "addBlock" method work for once
    @Unique private final HashMap<BlockPos,Boolean> moreStorageForCreate$checkedBlockPos = new HashMap<>();
    @Redirect(method = "moveBlock",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/Contraption;addBlock(Lnet/minecraft/core/BlockPos;Lorg/apache/commons/lang3/tuple/Pair;)V"),remap = false)
    public void storeBlock(Contraption instance, BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair){
        if(canBeControlledBlock(pair.getRight().getBlockState().getBlock().asItem())){
            addData(MathMethod.pos,pos);
            addData(MathMethod.pair,pair);
        }else this.addBlock(pos,pair);
    }
    @Inject(method = "moveBlock",at = @At("HEAD"),remap = false)
    public void setNeedAddBlock(Level world, Direction forcedDirection, Queue<BlockPos> frontier, Set<BlockPos> visited, CallbackInfoReturnable<Boolean> cir){
        moreStorageForCreate$waitAddBlock = false;
    }
    @Inject(method = "searchMovedStructure",at = @At("RETURN"),remap = false)
    // To add storage block after "searchMovedStructure" (use a variant to do this)

    public void addBlock(Level world, BlockPos pos, Direction forcedDirection, CallbackInfoReturnable<Boolean> cir) throws AssemblyException {
        if(moreStorageForCreate$waitAddBlock)
            return;
        if (MathMethod.pos.isEmpty() || pair.isEmpty() || pair.size() != pair.size()) {
            resetStatus();
            throw new AssemblyException("The block status is broken or doesn't have block status at all!", new Exception());
        }
        Block thisBlock;
        for (int i = 0; i < MathMethod.pos.size(); i++) {
            thisBlock = pair.get(i).getRight().getBlockState().getBlock();
            pos = MathMethod.pos.get(i);
            if (canBeControlledBlock(thisBlock.asItem()))
                moreStorageForCreate$skipAdd = moreStorageForCreate$checkAddToStorage(world,pos,thisBlock,MoreContraptionStorageConfig.CheckAdjacentBlock.get());
            this.addBlock(pos,pair.get(i));
            moreStorageForCreate$checkedBlockPos.put(pos,moreStorageForCreate$skipAdd);
        }
        moreStorageForCreate$waitAddBlock = true;
        moreStorageForCreate$checkedBlockPos.clear();
        resetStatus();
    }
    @Unique
    public boolean moreStorageForCreate$checkAddToStorage(Level world, BlockPos thisPos, Block thisBlock, boolean search){
        for (BlockPos block : getAroundedBlockPos(thisPos)) {
            Boolean checked = moreStorageForCreate$checkedBlockPos.get(thisPos);
            if(checked != null)
                return checked;
            BlockEntity Block = world.getBlockEntity(block);
            if (Block instanceof ContraptionControlsBlockEntity &&
                    ((ContraptionControlsBlockEntity) Block).filtering.getFilter().getItem() == thisBlock.asItem() &&
                    MoreContraptionStorageConfig.getDefaultOpen(((ContraptionControlsBlockEntity) Block).disabled)) {
                return true;
            }
            if (search && Block != null &&  thisBlock == Block.getBlockState().getBlock()) {
                return searchBlockPos(world,thisPos,
                        (level,pos,initialBlock) -> thisBlock == level.getBlockState(pos).getBlock(),
                        (level,pos,initialBlock,nowReturnValue,t) -> {
                            if (t == null)
                                t = false;
                            if (nowReturnValue == null)
                                nowReturnValue = false;
                            return nowReturnValue || t || moreStorageForCreate$checkAddToStorage(level, pos, thisBlock, false);
                        },
                        (level, pos, blockState,returnValue)-> moreStorageForCreate$checkedBlockPos.put(pos, returnValue));
            }
        }
        return false;
    }
}