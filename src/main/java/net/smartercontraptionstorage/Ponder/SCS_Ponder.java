package net.smartercontraptionstorage.Ponder;

//import Excludes.BuildNBTFile;
import appeng.api.ids.AEBlockIds;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderTag;
import com.simibubi.create.foundation.ponder.PonderTagRegistry;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.smartercontraptionstorage.SmarterContraptionStorage;

import static net.smartercontraptionstorage.Utils.asResources;

public class SCS_Ponder {
    private static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(SmarterContraptionStorage.MODID);
    public static final PonderTag CONTROLLABLE_CONTAINERS = new PonderTag(asResources("controllable_containers")).item(Blocks.CHEST).defaultLang("Controllable Containers","Containers can be controlled by Contraption Controls").addToIndex();
    public static final PonderTagRegistry.TagBuilder BUILDER = PonderRegistry.TAGS.forTag(CONTROLLABLE_CONTAINERS);
    public static void register(){
        registerTags();
        HELPER.forComponents(AllBlocks.CONTRAPTION_CONTROLS).addStoryBoard("storage_control", MovementActorScenes::controlStorageBlock,CONTROLLABLE_CONTAINERS);
        HELPER.forComponents(AllBlocks.CONTRAPTION_CONTROLS).addStoryBoard("ordinary_control", MovementActorScenes::changeOrdinary,CONTROLLABLE_CONTAINERS);
        HELPER.forComponents(AllBlocks.TOOLBOXES).addStoryBoard("replenish_item",ToolboxScenes::replenishItem,CONTROLLABLE_CONTAINERS);
        //BuildNBTFile.createNBTFile();
    }
    public static void registerTrashCan(){
        HELPER.forComponents(AllBlocks.TOOLBOXES).addStoryBoard("trash_control", ToolboxScenes::trashcansControl, CONTROLLABLE_CONTAINERS);
    }
    public static void registerAE(){
        HELPER.addStoryBoard(AEBlockIds.CONTROLLER, "use_ae",AEScenes::useAE);
        HELPER.addStoryBoard(AEBlockIds.SPATIAL_PYLON,"spatial_cell",AEScenes::spatialCell);
        BUILDER.add(AEBlockIds.CONTROLLER).add(AEBlockIds.SPATIAL_PYLON);
    }
    public static void registerTags(){
        BUILDER.add(AllBlocks.CONTRAPTION_CONTROLS).add(Blocks.CHEST).add(Blocks.TRAPPED_CHEST).add(Blocks.BARREL).add(AllBlocks.ITEM_VAULT).add(AllBlocks.TOOLBOXES.get(DyeColor.BROWN));
    }
}