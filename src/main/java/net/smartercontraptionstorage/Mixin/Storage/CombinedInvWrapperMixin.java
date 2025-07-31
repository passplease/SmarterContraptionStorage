package net.smartercontraptionstorage.Mixin.Storage;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.smartercontraptionstorage.AddStorage.FluidHander.DumpHandler;
import net.smartercontraptionstorage.AddStorage.ItemHandler.StorageHandlerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CombinedInvWrapper.class)
public interface CombinedInvWrapperMixin {
    @Accessor("itemHandler")
    IItemHandlerModifiable[] getHandlers();

    @Mixin(MountedItemStorageWrapper.class)
    abstract class MountedItemStorageWrapperMixin extends CombinedInvWrapper {
        @ModifyArg(method = "<init>",at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/wrapper/CombinedInvWrapper;<init>([Lnet/minecraftforge/items/IItemHandlerModifiable;)V"),remap = false)
        private static IItemHandlerModifiable[] addDumpHandler(IItemHandlerModifiable[] itemHandler){
            if(DumpHandler.isOpened()) {
                IItemHandlerModifiable[] handlers = new IItemHandlerModifiable[itemHandler.length + 1];
                handlers[0] = StorageHandlerHelper.NULL_HANDLER;
                System.arraycopy(itemHandler, 0, handlers, 1, itemHandler.length);
                return handlers;
            }
            return itemHandler;
        }
    }
}
