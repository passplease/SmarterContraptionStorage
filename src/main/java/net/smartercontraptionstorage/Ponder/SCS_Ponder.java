package net.smartercontraptionstorage.Ponder;

//import Excludes.BuildNBTFile;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.smartercontraptionstorage.SmarterContraptionStorage;

public class SCS_Ponder {
    private static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(SmarterContraptionStorage.MODID);
    public static final PonderTag CONTROLLABLE_CONTAINERS;
    static {
        CONTROLLABLE_CONTAINERS = new PonderTag(asResources("controllable_containers")).item(Blocks.CHEST).defaultLang("Controllable Containers","Containers can be controlled by Contraption Controls").addToIndex();
    }
    public static ResourceLocation asResources(String name){
        return new ResourceLocation(SmarterContraptionStorage.MODID,name);
    }
    public static void register(){
        registerTags();
        HELPER.forComponents(AllBlocks.CONTRAPTION_CONTROLS).addStoryBoard("storage_control", MovementActorScenes::controlStorageBlock,CONTROLLABLE_CONTAINERS);
        HELPER.forComponents(AllBlocks.TOOLBOXES).addStoryBoard("replenish_item",ToolboxScenes::replenishItem,CONTROLLABLE_CONTAINERS);
        //BuildNBTFile.createNBTFile();
    }
    public static void registerTrashCan(){
        HELPER.forComponents(AllBlocks.TOOLBOXES).addStoryBoard("trash_control", ToolboxScenes::trashcansControl, CONTROLLABLE_CONTAINERS);
    }
    public static void registerTags(){
        PonderRegistry.TAGS.forTag(CONTROLLABLE_CONTAINERS).add(AllBlocks.CONTRAPTION_CONTROLS).add(Blocks.CHEST).add(Blocks.TRAPPED_CHEST).add(Blocks.BARREL).add(AllBlocks.ITEM_VAULT);
    }
}