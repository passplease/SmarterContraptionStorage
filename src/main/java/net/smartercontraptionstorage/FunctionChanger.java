package net.smartercontraptionstorage;

import com.simibubi.create.content.contraptions.MountedStorage;
import net.minecraft.world.level.block.entity.BlockEntity;

// A class to do the same job as AccessTransformer
// After I know how to use AT, I will delete this class immediately
public final class FunctionChanger {
    private static boolean mountedEntity = false;
    private static BlockEntity mounted_entity;
    public static BlockEntity findMountedEntity(MountedStorage storage){
        mountedEntity = true;
        mounted_entity = null;
        storage.isValid();
        mountedEntity = false;
        return mounted_entity;
    }
    public static boolean isMountedEntity() {
        return mountedEntity;
    }
    public static void setMounted_entity(BlockEntity entity){
        mounted_entity = entity;
    }
}