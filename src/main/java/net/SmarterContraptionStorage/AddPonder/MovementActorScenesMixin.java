package net.SmarterContraptionStorage.AddPonder;

import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;

public class MovementActorScenesMixin {
    public static void controlStorageBlock(SceneBuilder scene, SceneBuildingUtil util){
        scene.title("storage_control","How to control storage on contraptions");
        scene.configureBasePlate(1,0,6);
        scene.scaleSceneView(0.9F);
        scene.showBasePlate();
    }
}