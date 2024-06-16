package net.morestorageforcreate.Mixin.Ponder;

import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.infrastructure.ponder.scenes.MovementActorScenes;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MovementActorScenes.class)
public class MovementActorScenesMixin {
    protected static void controlStorageBlock(SceneBuilder scene, SceneBuildingUtil util){

    }
}
