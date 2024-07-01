package net.smartercontraptionstorage.Ponder;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.drill.DrillBlockEntity;
import com.simibubi.create.foundation.ponder.*;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.ponder.element.WorldSectionElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Items;

public class ToolboxScenes {
    public static void trashcansControl(SceneBuilder scene, SceneBuildingUtil util){
        scene.title("trash_control","Help Control TrashCans");
        scene.configureBasePlate(0,0,5);
        scene.scaleSceneView(0.9F);
        scene.showBasePlate();
        scene.idle(5);
        BlockPos shaft = util.grid.at(3,1,3);
        BlockPos gearshift = util.grid.at(3,1,4);
        Selection kinetics = util.select.fromTo(3,1,2,3,1,4);
        BlockPos lever = util.grid.at(3,2,4);
        Selection cogwheel = util.select.fromTo(3,1,5,4,0,5);
        BlockPos[] cobblestone = {util.grid.at(1,1,2),util.grid.at(0,1,2)};
        BlockPos toolbox = util.grid.at(3,1,1);
        BlockPos trashcan = util.grid.at(2,1,1);
        BlockPos drill = util.grid.at(2,1,1);
        scene.world.modifyKineticSpeed(util.select.position(gearshift).add(cogwheel),(speed) -> speed);
        scene.idle(5);
        scene.world.showSection(cogwheel, Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(gearshift),Direction.SOUTH);
        scene.world.showSection(util.select.position(lever),Direction.SOUTH);
        scene.idle(5);
        scene.world.showSection(util.select.position(shaft),Direction.SOUTH);
        scene.idle(5);
        ElementLink<WorldSectionElement> contraption = scene.world.showIndependentSection(util.select.position(3,1,1),Direction.DOWN);
        scene.world.moveSection(contraption,util.vector.of(0,0,1),0);
        scene.world.showSection(util.select.position(3,1,2),Direction.DOWN);
        scene.idle(5);
        scene.world.showSectionAndMerge(util.select.position(4,1,1),Direction.DOWN,contraption);
        scene.idle(5);
        scene.world.showSectionAndMerge(util.select.position(5,1,1),Direction.DOWN,contraption);
        scene.idle(5);
        scene.world.showSectionAndMerge(util.select.fromTo(trashcan.north(),toolbox.north()).add(util.select.position(drill)),Direction.DOWN,contraption);
        scene.world.showSection(util.select.position(cobblestone[0]),Direction.UP);
        scene.idle(22);
        scene.addKeyframe();
        scene.overlay.showOutline(PonderPalette.GREEN, AllItems.SUPER_GLUE,util.select.fromTo(trashcan,toolbox).add(util.select.position(drill.south())),25);
        scene.overlay.showControls(new InputWindowElement(util.vector.centerOf(trashcan), Pointing.DOWN).withItem(AllItems.SUPER_GLUE.asStack()),30);
        scene.idle(40);
        scene.overlay.showText(50).placeNearTarget().pointAt(util.vector.topOf(trashcan).add(0,-0.25,0)).text("Normally every item will be deleted if there is no any other containers except trashcan");
        scene.idle(60);
        scene.effects.indicateRedstone(lever);
        scene.world.toggleRedstonePower(util.select.fromTo(lever,gearshift));
        scene.world.modifyKineticSpeed(kinetics,(speed) -> -speed);
        scene.world.moveSection(contraption,util.vector.of(-0.1,0,0),2);
        scene.world.modifyBlockEntity(drill, DrillBlockEntity.class,(block) -> block.setSpeed(32));
        scene.idle(2);
        for (int i = 0; i < 10; i++) {
            scene.idle(3);
            scene.world.incrementBlockBreakingProgress(cobblestone[0]);
        }
        scene.world.moveSection(contraption,util.vector.of(-1.9,0,0),38);
        scene.idle(10);
        scene.overlay.showText(30).independent(70).text("no drops are spawned ...");
        scene.idle(28);
        scene.world.modifyBlockEntity(drill, DrillBlockEntity.class,(block) -> block.setSpeed(0));
        scene.idle(5);
        scene.effects.indicateRedstone(lever);
        scene.world.toggleRedstonePower(util.select.fromTo(lever,gearshift));
        scene.world.modifyKineticSpeed(kinetics,(speed) -> -speed);
        scene.world.moveSection(contraption,util.vector.of(2,0,0),40);
        scene.idle(42);
        scene.addKeyframe();
        scene.overlay.showText(150).placeNearTarget().pointAt(util.vector.topOf(trashcan).add(0,-0.25,0)).text("Although we can use the filter trashcan provided to us, it may not enough to use. And also, any items matches contents in toolbox we wouldn't want them be deleted, so we can use toolbox as an extended black filter. Let's see how to use it");
        scene.idle(160);
        scene.overlay.showText(70).placeNearTarget().pointAt(util.vector.centerOf(toolbox)).text("If we add cobblestone to our toolbox (actually, only need to set it's filter)");
        scene.idle(30);
        scene.overlay.showControls(new InputWindowElement(util.vector.centerOf(toolbox),Pointing.UP).withItem(Items.COBBLESTONE.getDefaultInstance()),40);
        scene.idle(45);
        scene.overlay.showText(90).placeNearTarget().pointAt(util.vector.topOf(trashcan).add(0,-0.25,0)).text("Then this cobblestone will drop on the ground rather than deleted by trashcan (toolbox can be anywhere on contraption, don't need to place near trashcan)");
        scene.idle(5);
        scene.world.showSection(util.select.position(cobblestone[1]),Direction.UP);
        scene.idle(90);
        scene.effects.indicateRedstone(lever);
        scene.world.toggleRedstonePower(util.select.fromTo(lever,gearshift));
        scene.world.modifyKineticSpeed(kinetics,(speed) -> -speed);
        scene.world.moveSection(contraption,util.vector.of(-1.1,0,0),22);
        scene.world.modifyBlockEntity(drill, DrillBlockEntity.class,(block) -> block.setSpeed(32));
        scene.idle(32);
        for (int i = 0; i < 10; i++) {
            scene.idle(3);
            scene.world.incrementBlockBreakingProgress(cobblestone[1]);
        }
        scene.world.createItemEntity(util.vector.centerOf(cobblestone[1]),util.vector.of(-0.1,0,0),Items.COBBLESTONE.getDefaultInstance());
        scene.world.moveSection(contraption,util.vector.of(-0.9,0,0),18);
        scene.idle(18);
        scene.world.modifyBlockEntity(drill,DrillBlockEntity.class,(block) -> block.setSpeed(0));
        scene.idle(5);
    }
}