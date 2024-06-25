package net.SmarterContraptionStorage.AddStorage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public abstract class StorageHandlerHelper {
    public static final Set<StorageHandlerHelper> HandlerHelpers = new HashSet<>();
    public static void register(@NotNull StorageHandlerHelper Entity){
        HandlerHelpers.add(Entity);
    }
    public static void register(){
        register(new DrawersHandlerHelper());
    }
    public static boolean canControl(Item comparedItem){
        for(StorageHandlerHelper handlerHelper : HandlerHelpers){
            if(handlerHelper.allowControl(comparedItem))
                return true;
        }
        return false;
    }
    public static boolean canUseModdedInventory(BlockEntity entity){
        for(StorageHandlerHelper handlerHelper : HandlerHelpers){
            if(handlerHelper.canCreateHandler(entity))
                return true;
        }
        return false;
    }
    public static @Nullable StorageHandlerHelper findSuitableHelper(BlockEntity entity){
        for(StorageHandlerHelper handlerHelper : HandlerHelpers)
            if(handlerHelper.canCreateHandler(entity))
                return handlerHelper;
        return null;
    }
    protected abstract boolean canCreateHandler(BlockEntity Entity);
    public abstract void addStorageToWorld(BlockEntity entity,ItemStackHandler handler);
    public abstract ItemStackHandler createHandler(BlockEntity entity);
    public CompoundTag serialize(CompoundTag tag){return tag;}
    protected abstract boolean allowControl(Item comparedItem);
}