package net.morestorageforcreate;

import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;

public class Status {
    public static HashMap<Integer, Pair<StructureTemplate.StructureBlockInfo, BlockEntity>> pair = new HashMap<>();
    public static HashMap<Integer, BlockPos> pos = new HashMap<>();
    public static <T> void put(HashMap<Integer,T> map,T t){
        map.put(map.size(), t);
    }
    public static void resetAllStatus(){
        pair.clear();
        pos.clear();
    }
}
