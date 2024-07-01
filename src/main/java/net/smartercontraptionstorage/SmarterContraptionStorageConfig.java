package net.smartercontraptionstorage;

import net.minecraftforge.common.ForgeConfigSpec;

public class SmarterContraptionStorageConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue DEFAULT_OPEN;
    public static final ForgeConfigSpec.BooleanValue CHECK_ADJACENT_BLOCK;
    public static final ForgeConfigSpec.ConfigValue<Integer> SEARCH_RANGE;
    static {
        BUILDER.push("Config for More legal Storage on Contraption !");// push会创建一个缩进
        DEFAULT_OPEN = BUILDER.comment("""
                Weather default uses all storage blocks
                Instructions:
                If you set it true,the mod will open all of you contraption's storage by default
                If you set it false, your block will close by default,also,as the result you can't open it until they become block.
                And in any circumstances you can use Contraption Control Block to change the action it performed
                """).define("DefaultOpen",true);
        CHECK_ADJACENT_BLOCK = BUILDER.comment("Check neighboring storage block automatically (such as Vault):").define("CheckAdjacentBlock",true);
        SEARCH_RANGE = BUILDER.comment("Search range for neighboring block (best bigger than 9, because Vault maxsize is 9)").define("SearchRange",10);
        BUILDER.pop();// pop会解除缩进
        SPEC = BUILDER.build();
    }
    public static boolean getDefaultOpen(boolean open){
        return open == DEFAULT_OPEN.get();
    }
}