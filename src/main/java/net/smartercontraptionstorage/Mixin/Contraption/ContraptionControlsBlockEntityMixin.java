package net.smartercontraptionstorage.Mixin.Contraption;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeItem;
import net.smartercontraptionstorage.Interface.Changeable;
import net.smartercontraptionstorage.Render.Overlay;
import net.smartercontraptionstorage.Render.OverlayHolder;
import net.smartercontraptionstorage.SmarterContraptionStorage;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(ContraptionControlsBlockEntity.class)
@Implements(@Interface(iface = OverlayHolder.class,prefix = SmarterContraptionStorage.MODID + '$',unique = true))
public abstract class ContraptionControlsBlockEntityMixin implements Changeable,OverlayHolder {
    @Shadow(remap = false) public FilteringBehaviour filtering;
    @Unique
    @Nullable
    Overlay smarterContraptionStorage$overlay = null;

    @Inject(method = "addBehaviours",at = @At("RETURN"),remap = false)
    public void withPredicate(List<BlockEntityBehaviour> behaviours, CallbackInfo ci){
        this.filtering.withPredicate((itemStack)-> AllTags.AllItemTags.CONTRAPTION_CONTROLLED.matches(itemStack) || Utils.canBeControlledItem(itemStack.getItem()));
    }

    @Deprecated
    @Override
    public @Nullable Object get(String name) {
        if(Objects.equals(name, "overlay"))
            return smarterContraptionStorage$overlay;
        return null;
    }
    @Deprecated
    @Override
    public void set(Object object) {
        if(object instanceof Overlay) {
            this.smarterContraptionStorage$overlay = (Overlay) object;
        }
    }
    @Deprecated
    @Override
    public void set(String parameterName, Object object) {
        if(Objects.equals(parameterName, "overlay")){
            if(object instanceof DyeItem item)
                this.smarterContraptionStorage$overlay = Overlay.get(item);
            else if(object == null)
                this.smarterContraptionStorage$overlay = null;
        }else set(object);
    }

    @Inject(method = "read",at = @At("HEAD"),remap = false)
    protected void read(CompoundTag tag, boolean clientPacket, CallbackInfo ci){
        this.smarterContraptionStorage$overlay = Overlay.get(tag.getString("overlay"));
    }

    @Inject(method = "write",at = @At("HEAD"),remap = false)
    protected void write(CompoundTag tag, boolean clientPacket, CallbackInfo ci){
        if(smarterContraptionStorage$overlay != null)
            tag.putString("overlay", this.smarterContraptionStorage$overlay.getName());
    }

    @Unique
    public Overlay smartercontraptionstorage$getOverlay() {
        return smarterContraptionStorage$overlay;
    }

    @Unique
    public void smartercontraptionstorage$setOverlay(Overlay overlay) {
        smarterContraptionStorage$overlay = overlay;
    }

    @Unique
    public void smartercontraptionstorage$setOverlay(@Nullable DyeItem dye) {
        smarterContraptionStorage$overlay = Overlay.get(dye);
    }
}
