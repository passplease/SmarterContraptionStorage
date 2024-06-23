package net.SmarterContraptionStorage.AddPonder;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.foundation.ponder.*;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.ponder.element.WorldSectionElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class MovementActorScenesMixin {
    public static void controlStorageBlock(SceneBuilder scene, SceneBuildingUtil util){
        scene.title("storage_control","w to control storage on contraptions");
        scene.configureBasePlate(0,0,5);
        scene.scaleSceneView(0.9F);
        scene.showBasePlate();
        scene.idle(5);
        BlockPos shaft = util.grid.at(3,1,3);
        BlockPos gearshift = util.grid.at(3,1,4);
        Selection kinetics = util.select.fromTo(3,1,2,3,1,4);
        BlockPos[] log = new BlockPos[]{util.grid.at(1,1,2),util.grid.at(1,1,3)};
        BlockPos lever = util.grid.at(3,2,4);
        Selection cogwheel = util.select.fromTo(3,1,5,4,0,5);
        BlockPos[] saw = new BlockPos[]{util.grid.at(2,1,1),util.grid.at(2,1,2)};
        BlockPos chest = util.grid.at(2,1,0);
        BlockPos control = util.grid.at(3,1,0);
        scene.world.modifyKineticSpeed(util.select.position(gearshift).add(cogwheel),(speed) -> speed);
        scene.idle(5);
        scene.world.showSection(cogwheel,Direction.DOWN);
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
        scene.world.showSectionAndMerge(util.select.fromTo(chest,util.grid.at(2,1,2)).add(util.select.position(control)),Direction.DOWN,contraption);
        scene.world.showSection(util.select.fromTo(log[0],log[1]),Direction.UP);
        scene.idle(20);
        control = control.south();
        chest = chest.south();
        scene.overlay.showOutline(PonderPalette.GREEN,"glue",util.select.fromTo(2,1,1,2,1,3).add(util.select.position(control)),20);
        scene.overlay.showControls((new InputWindowElement(util.vector.centerOf(control), Pointing.RIGHT)).withItem(AllItems.SUPER_GLUE.asStack()),30);
        scene.idle(40);
        scene.effects.indicateRedstone(lever);
        scene.world.toggleRedstonePower(util.select.fromTo(lever,gearshift));
        scene.world.modifyKineticSpeed(kinetics,(speed) -> -speed);
        scene.world.moveSection(contraption,util.vector.of(-0.1,0,0),2);
        scene.world.modifyBlockEntity(saw[0], SawBlockEntity.class,(block) -> block.setSpeed(32));
        scene.world.modifyBlockEntity(saw[1], SawBlockEntity.class,(block) -> block.setSpeed(32));
        scene.effects.rotationDirectionIndicator(shaft);
        scene.idle(2);
        for (int i = 0; i < 10; i++) {
            scene.idle(3);
            scene.world.incrementBlockBreakingProgress(log[0]);
            scene.world.incrementBlockBreakingProgress(log[1]);
        }
        scene.world.moveSection(contraption,util.vector.of(-1.9,0,0),38);
        scene.idle(38);
        scene.world.modifyBlockEntity(saw[0], SawBlockEntity.class,(block) -> block.setSpeed(0));
        scene.world.modifyBlockEntity(saw[1], SawBlockEntity.class,(block) -> block.setSpeed(0));
        scene.overlay.showText(60).pointAt(util.vector.topOf(chest.west(2)).add(0,-0.15,0)).placeNearTarget().text("Items will be stored in contraptionLink's container automatically");
        scene.idle(5);
        scene.overlay.showControls((new InputWindowElement(util.vector.topOf(chest.west(2)),Pointing.DOWN)).withItem(Items.DARK_OAK_LOG.getDefaultInstance()),35);
        scene.idle(70);
        scene.effects.indicateRedstone(lever);
        scene.world.toggleRedstonePower(util.select.fromTo(lever,gearshift));
        scene.world.modifyKineticSpeed(kinetics,(speed) -> -speed);
        scene.effects.rotationDirectionIndicator(shaft);
        scene.world.moveSection(contraption,util.vector.of(2,0,0),40);
        scene.idle(50);
        scene.overlay.showText(60).pointAt(util.vector.topOf(control).add(0,-0.25,0)).placeNearTarget().text("Using this mod, you could control every container behaviour on contraptionLink. Here let's use drawer right click controls block to set its value");
        scene.addKeyframe();
        scene.idle(70);
        scene.overlay.showControls((new InputWindowElement(util.vector.topOf(control),Pointing.DOWN)).rightClick().withItem(Items.CHEST.getDefaultInstance()),30);
        scene.idle(20);
        scene.world.setFilterData(util.select.position(control.north()), ContraptionControlsBlockEntity.class,Items.CHEST.getDefaultInstance());
        scene.overlay.showText(60).pointAt(util.vector.topOf(control)).placeNearTarget().text("And now, this block will control our chest");
        scene.idle(20);
        scene.overlay.showControls((new InputWindowElement(util.vector.centerOf(control),Pointing.UP)).rightClick(),20);
        scene.idle(25);
        scene.world.modifyBlockEntity(control.north(),ContraptionControlsBlockEntity.class,(entity) -> entity.disabled = true);
        for (int i = log.length - 1; i >= 0; i--) {
            log[i] = log[i].west();
            scene.world.showSection(util.select.position(log[i]),Direction.DOWN);
        }
        scene.idle(35);
        scene.overlay.showText(60).placeNearTarget().pointAt(util.vector.topOf(control).add(0,-0.25,0)).independent(60).text("If we release piston now,the chest won't be added to contraption. So these logs will drop on the ground");
        scene.idle(70);
        scene.effects.indicateRedstone(lever);
        scene.world.toggleRedstonePower(util.select.fromTo(lever,gearshift));
        scene.world.modifyKineticSpeed(kinetics,(speed) -> -speed);
        scene.world.moveSection(contraption,util.vector.of(-1.1,0,0),22);
        scene.world.modifyBlockEntity(saw[0], SawBlockEntity.class,(block) -> block.setSpeed(32));
        scene.world.modifyBlockEntity(saw[1], SawBlockEntity.class,(block) -> block.setSpeed(32));
        scene.effects.rotationDirectionIndicator(shaft);
        scene.idle(22);
        for (int i = 0; i < 10; i++) {
            scene.idle(3);
            scene.world.incrementBlockBreakingProgress(log[0]);
            scene.world.incrementBlockBreakingProgress(log[1]);
        }
        scene.world.createItemEntity(util.vector.centerOf(log[0]),util.vector.of(-0.1,0,0),Items.DARK_OAK_LOG.getDefaultInstance());
        scene.world.createItemEntity(util.vector.centerOf(log[1]),util.vector.of(-0.1,0,0),Items.DARK_OAK_LOG.getDefaultInstance());
        scene.overlay.showText(18).placeNearTarget().independent(50).text("It works as we predicate...");
        scene.world.moveSection(contraption,util.vector.of(-0.9,0,0),18);
        scene.idle(18);
        scene.world.modifyBlockEntity(saw[0], SawBlockEntity.class,(block) -> block.setSpeed(0));
        scene.world.modifyBlockEntity(saw[1], SawBlockEntity.class,(block) -> block.setSpeed(0));
        scene.addKeyframe();
        scene.idle(5);
        scene.overlay.showText(60).pointAt(util.vector.topOf(chest.west(2))).colored(PonderPalette.RED).placeNearTarget().text("I need to point out if we close some container we won't open it as long as it is still on contraption.");
        scene.idle(70);
        scene.overlay.showText(120).placeNearTarget().independent(50).text("Some other things, any actors on contraption can use these containers. You can change weather close them until controls block opened by using config file");
    }
}