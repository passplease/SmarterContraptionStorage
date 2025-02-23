package net.smartercontraptionstorage;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;

public final class FunctionChanger {
    /**
     * To serialize some MountedStorage,
     * value set in MountedStorageManagerMixin,
     * used in MountedStorageMixin, MountedFluidStorageMixin
     */
    public static Map<BlockPos, BlockEntity> presentBlockEntities;
}