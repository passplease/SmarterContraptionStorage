package net.morestorageforcreate;

import net.minecraftforge.common.ForgeConfigSpec;

public class MoreContraptionStorageConfig {
    public static final ForgeConfigSpec.Builder Builder = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC = Builder.build();
    private static final ForgeConfigSpec.BooleanValue DefaultOpen;
    static {
        Builder.push("Config for More legal Storage on Contraption !");// push会创建一个缩进
        DefaultOpen = Builder.comment("Weather default uses all storage blocks:").define("defaultOpen",true);
        Builder.pop();// pop会解除缩进
    }
    public static boolean getDefaultOpen(boolean open){
        return open != DefaultOpen.get();
    }
}
