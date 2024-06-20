package net.SmarterContraptionStorage.AddPonder;

import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class MovementActorScenesMixin {
    public static void controlStorageBlock(SceneBuilder scene, SceneBuildingUtil util){
        scene.title("storage_control","How to control storage on contraptions");
        scene.configureBasePlate(0,0,7);
        scene.scaleSceneView(0.9F);
        scene.showBasePlate();
        scene.idle(5);
        BlockPos piston = util.grid.at(6,1,2);
        Selection power = util.select.fromTo(7,-1,7,7,2,8);
        Selection contraption = util.select.fromTo(5,1,1,5,2,5).add(util.select.position(piston));
        scene.world.showSection(contraption, Direction.SOUTH);
        scene.world.showSection(power,Direction.SOUTH);
    }
}