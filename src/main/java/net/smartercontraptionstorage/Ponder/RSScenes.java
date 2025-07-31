package net.smartercontraptionstorage.Ponder;

import com.refinedmods.refinedstorage.RSItems;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.drill.DrillBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;

import static net.minecraft.core.Direction.DOWN;
import static net.minecraft.core.Direction.UP;

public class RSScenes {
    public static void useRS(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("use_rs","How to use RS on contraption");
        scene.world().showSection(util.select().fromTo(0,0,0,5,0,5),UP);
        scene.overlay().showText(50).independent(70).text("First, let's build a small RS net");
        BlockPos importBus = util.grid().at(2,1,3);
        BlockPos exportBus = util.grid().at(3,1,3);
        BlockPos drill = util.grid().at(1,1,3);
        Selection shaft = util.select().fromTo(2,3,3,5,3,3).add(util.select().fromTo(6,-2,3,6,3,3));
        BlockPos stone = util.grid().at(0,1,3);
        scene.idle(15);
        ElementLink<WorldSectionElement> contraption = scene.world().showIndependentSection(util.select().position(2,1,2),DOWN);
        scene.idle(5);
        scene.world().showSectionAndMerge(util.select().position(2,1,1),DOWN,contraption);
        scene.idle(5);
        scene.world().showSectionAndMerge(util.select().position(3,1,2),DOWN,contraption);
        scene.idle(5);
        scene.world().showSectionAndMerge(util.select().fromTo(exportBus,importBus),UP,contraption);
        scene.idle(25);
        scene.overlay().showText(40).placeNearTarget().pointAt(util.vector().topOf(exportBus)).text("Export Bus");
        scene.overlay().showText(40).placeNearTarget().pointAt(util.vector().topOf(importBus)).text("Import Bus");
        scene.idle(50);
        scene.overlay().showText(100).pointAt(util.vector().topOf(importBus)).placeNearTarget().attachKeyFrame().text("These bus is important for contraptions to locate the proper RS Net of the world. And due to the input and output RS Net can be different, so you should set them separately");
        scene.idle(110);
        scene.overlay().showControls(util.vector().topOf(importBus), Pointing.DOWN,50).withItem(RSItems.WIRELESS_GRID.get().getDefaultInstance());
        scene.overlay().showControls(util.vector().topOf(exportBus), Pointing.DOWN,50).withItem(RSItems.WIRELESS_GRID.get().getDefaultInstance());
        scene.overlay().showText(100).pointAt(util.vector().topOf(importBus)).placeNearTarget().text("Buses also need setup, they should be all filled with Speed Card Upgrade and Wireless Grid ");
        scene.idle(110);
        scene.overlay().showOutline(PonderPalette.GREEN, AllItems.SUPER_GLUE,util.select().fromTo(2,1,1,3,1,3),50);
        scene.overlay().showText(100).placeNearTarget().pointAt(util.vector().centerOf(3,1,2)).text("Adding controller and disk driver (required, interface optional) so that your contraption can connect to RS Net");
        scene.idle(110);
        scene.addKeyframe();
        scene.world().showSectionAndMerge(util.select().position(drill),UP,contraption);
        scene.world().showSection(util.select().position(stone),UP);
        scene.idle(5);
        scene.world().showSection(shaft,DOWN);
        scene.world().showSectionAndMerge(util.select().position(3,2,3),DOWN,contraption);
        scene.idle(10);
        scene.world().modifyBlockEntity(drill, DrillBlockEntity.class, entity -> entity.setSpeed(32F));
        scene.world().moveSection(contraption,util.vector().of(-0.2,0,0),4);
        scene.idle(4);
        for (int i = 0; i < 10; i++) {
            scene.idle(3);
            scene.world().incrementBlockBreakingProgress(stone);
        }
        scene.world().moveSection(contraption,util.vector().of(-0.8,0,0),16);
        scene.overlay().showText(136).placeNearTarget().pointAt(util.vector().topOf(stone)).text("Items will be stored in the RS Net which is connecting to Wireless Grid in Import Bus. And I have to point out, each action that contraption try to insert or extract items consumes 100 energy of RS net");
        scene.idle(16);
        scene.world().modifyBlockEntity(drill, DrillBlockEntity.class,entity -> entity.setSpeed(0F));
        scene.idle(120);
        scene.overlay().showText(80).pointAt(util.vector().topOf(2,1,2)).text("One more thing, Interface can set the white filter for extracting RS Net ");
        scene.idle(80);
    }
}
