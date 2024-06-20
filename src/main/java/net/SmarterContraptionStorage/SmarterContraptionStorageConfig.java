package net.SmarterContraptionStorage;

import net.minecraftforge.common.ForgeConfigSpec;

public class SmarterContraptionStorageConfig {
    public static final ForgeConfigSpec.Builder Builder = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.BooleanValue DefaultOpen;
    public static final ForgeConfigSpec.BooleanValue CheckAdjacentBlock;
    public static final ForgeConfigSpec.ConfigValue<Integer> SearchRange;
    static {
        Builder.push("Config for More legal Storage on Contraption !");// push会创建一个缩进
        DefaultOpen = Builder.comment("""
                Weather default uses all storage blocks
                Instructions:
                If you set it true,the mod will open all of you contraption's storage by default
                If you set it false, your block will close by default,also,as the result you can't open it until they become block.
                And in any circumstances you can use Contraption Control Block to change the action it performed
                """).define("DefaultOpen",true);
        CheckAdjacentBlock = Builder.comment("Check neighboring storage block automatically (such as Vault):").define("CheckAdjacentBlock",true);
        SearchRange = Builder.comment("Search range for neighboring block (best bigger than 9, because Vault maxsize is 9)").define("SearchRange",10);
        Builder.pop();// pop会解除缩进
        SPEC = Builder.build();
    }
    public static boolean getDefaultOpen(boolean open){
        return open == DefaultOpen.get();
    }
}