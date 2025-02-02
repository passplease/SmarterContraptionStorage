package net.smartercontraptionstorage.Mixin.Contraption;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorage;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.smartercontraptionstorage.*;
import net.smartercontraptionstorage.Interface.Changeable;
import net.smartercontraptionstorage.Interface.Gettable;
import net.smartercontraptionstorage.Render.Overlay;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(Contraption.class)
public abstract class ContraptionMixin {
    @Shadow(remap = false) protected MountedStorageManager storage;
    @Shadow(remap = false) protected List<MutablePair<StructureTemplate.StructureBlockInfo, MovementContext>> actors;
    @Shadow(remap = false) protected abstract BlockPos toLocalPos(BlockPos globalPos);

    @Unique protected Map<Overlay,List<BlockPos>> smarterContraptionStorage$orderedBlocks = new HashMap<>();

    @Unique protected List<BlockPos> smarterContraptionStorage$removedBlocks = new ArrayList<>();
    @Inject(method = "searchMovedStructure",at = @At("RETURN"),remap = false)
    public void changeOrdinary(Level world, BlockPos pos, Direction forcedDirection, CallbackInfoReturnable<Boolean> cir){
        Changeable storage = (Changeable) this.storage;
        Map<BlockPos, MountedStorage> storages = (Map<BlockPos, MountedStorage>) storage.get("storage");
        assert storages != null;
        Map<BlockPos, MountedStorage> newStorage = new LinkedHashMap<>();
        smarterContraptionStorage$removedBlocks.forEach(storages::remove);
        if(!smarterContraptionStorage$orderedBlocks.isEmpty()) {
            List<MutablePair<StructureTemplate.StructureBlockInfo, MovementContext>> newActors = new LinkedList<>();
            Overlay.forEachSequentially((overlay) -> {
                List<BlockPos> list = smarterContraptionStorage$orderedBlocks.get(overlay);
                if(list != null)
                    for (BlockPos blockPos : list) {
                        Optional<MutablePair<StructureTemplate.StructureBlockInfo, MovementContext>> actor = actors.stream().filter((act) -> act.getLeft().pos.equals(blockPos)).findFirst();
                        // I think there is no any block could be used as actor and container at the same time
                        // So use if-else to reduce computation
                        if(actor.isPresent()) {
                            newActors.add(actor.get());
                            actors.remove(actor.get());
                        }else storages.computeIfPresent(blockPos, (p, s) -> {
                            newStorage.putIfAbsent(p,s);
                            return null;
                        });
                    }
            });
            newActors.addAll(actors);
            this.actors = newActors;
            newStorage.putAll(storages);
        }
        if(!smarterContraptionStorage$removedBlocks.isEmpty() || !newStorage.isEmpty())
            storage.set("storage", newStorage);
    }
    @Inject(method = "addBlock",at = @At("RETURN"),remap = false)
    public void addBlock(BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair, CallbackInfo ci){
        if(pair.getRight() instanceof ContraptionControlsBlockEntity entity && entity.getLevel() != null) {
            Level Level = entity.getLevel();
            if (((Gettable) entity).get("overlay") instanceof Overlay overlay) {
                List<Block> orderedBlock = new ArrayList<>();
                List<BlockPos> list = smarterContraptionStorage$orderedBlocks.getOrDefault(overlay,new ArrayList<>());
                Arrays.stream(Utils.getAroundedBlockPos(pos)).forEach(p -> orderedBlock.add(Level.getBlockState(p).getBlock()));
                Utils.searchBlockPos(Level, pos,
                        (level, thisPos, initialPos) -> orderedBlock.contains(level.getBlockState(thisPos).getBlock()),
                        (level, thisPos, initialPos, nowValue, returnValue) -> !Boolean.FALSE.equals(nowValue) && !Boolean.FALSE.equals(returnValue),
                        (level, thisPos, initialPos, returnValue) -> {
                            if (Boolean.TRUE.equals(returnValue))
                                list.add(this.toLocalPos(thisPos));
                            return null;
                        },
                        true
                );
            }
            if(Utils.canBeControlledItem(entity.filtering.getFilter().getItem()) && SmarterContraptionStorageConfig.getDefaultOpen(entity.disabled)){
                Block filterBlock = Block.byItem(entity.filtering.getFilter().getItem());
                Utils.searchBlockPos(Level,pos,
                        (level,thisPos,initialPos) -> level.getBlockState(thisPos).getBlock() == filterBlock,
                        (level,thisPos,initialPos,nowValue,returnValue) -> !Boolean.FALSE.equals(nowValue) && !Boolean.FALSE.equals(returnValue),
                        (level,thisPos,initialPos,returnValue) -> {
                    if(Boolean.TRUE.equals(returnValue))
                        smarterContraptionStorage$removedBlocks.add(this.toLocalPos(thisPos));
                    return null;
                        },
                        true
                );
            }
        }
    }
}