package net.smartercontraptionstorage;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorage;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;

// A class to do the same job as AccessTransformer
// If I know how to use AT, I will delete this class immediately
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
    @Deprecated
    private static boolean openGUI = false;
    @Deprecated
    private static Player player;
    @Deprecated
    private static Contraption contraption;
    @Deprecated
    private static MountedStorageManager storage;
    @Deprecated
    private static Map<BlockPos, MountedStorage> map;
    @Deprecated
    public static boolean openGUI(Contraption contraption, Player player, BlockPos pos){
        openGUI = true;
        FunctionChanger.contraption = contraption;
        FunctionChanger.player = player;
        contraption.isHiddenInPortal(BlockPos.ZERO);
        storage.getItems();
        boolean value = map.get(pos).canUseForFuel();
        FunctionChanger.player = null;
        openGUI = false;
        return value;
    }
    @Deprecated
    public static boolean isOpenGUI() {
        return openGUI;
    }
    @Deprecated
    public static Player getPlayer() {
        return player;
    }
    @Deprecated
    public static void setStorage(MountedStorageManager storage) {
        FunctionChanger.storage = storage;
    }
    @Deprecated
    public static void setMap(Map<BlockPos, MountedStorage> map) {
        FunctionChanger.map = map;
    }
    @Deprecated
    public static Contraption getContraption() {
        return contraption;
    }
    public static Map<BlockPos, BlockEntity> presentBlockEntities;
}