package net.morestorageforcreate.Mixin;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.morestorageforcreate.Status;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static net.morestorageforcreate.Status.*;

@Mixin(Contraption.class)
public abstract class ContraptionMixin {
    @Shadow(remap = false)
    protected abstract void addBlock(BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair);
//    @Inject(method = "moveBlock",
//            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/Contraption;addBlock(Lnet/minecraft/core/BlockPos;Lorg/apache/commons/lang3/tuple/Pair;)V"
//                    ,shift = At.Shift.BY
//                    ,by = -1),
//            remap = false)
//    private void setEntity(Level world, Direction forcedDirection, Queue<BlockPos> frontier, Set<BlockPos> visited, CallbackInfoReturnable<Boolean> cir){
//        Status.contraption = world;
//    }
    @Redirect(method = "moveBlock",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/Contraption;addBlock(Lnet/minecraft/core/BlockPos;Lorg/apache/commons/lang3/tuple/Pair;)V"),remap = false)
    private void store(Contraption instance, BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair){
        contraption = instance;
        try {
            Method addBlock = Contraption.class.getDeclaredMethod("addBlock", BlockPos.class, Pair.class);
            addBlock.setAccessible(true);
            if(pair.getValue().getBlockState().hasProperty(ChestBlock.TYPE) || pair.getValue() instanceof ContraptionControlsBlockEntity){
                put(Status.pair,pair);
                put(Status.pos,pos);
            }else addBlock.invoke(contraption,pos,pair);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Inject(method = "searchMovedStructure",at = @At("RETURN"),remap = false)
    private void addBlock(Level world, BlockPos pos, Direction forcedDirection, CallbackInfoReturnable<Boolean> cir){
        if(pair.isEmpty() || contraption == null || Status.pos.isEmpty()){
            resetAllStatus();
            return;
        }
        try {
            Method addBlock = Contraption.class.getDeclaredMethod("addBlock", BlockPos.class, Pair.class);
            addBlock.setAccessible(true);
            for(int i = 0; i < pair.size(); i++){
                addBlock.invoke(contraption,Status.pos.get(i), pair.get(i));
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        resetAllStatus();
    }
}
