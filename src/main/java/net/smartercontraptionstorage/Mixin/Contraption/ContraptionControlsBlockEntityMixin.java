package net.smartercontraptionstorage.Mixin.Contraption;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.smartercontraptionstorage.Render.Overlay;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ContraptionControlsBlockEntity.class)
public interface ContraptionControlsBlockEntityMixin {
    @Accessor("smarterContraptionStorage$overlay")
    void setOverlay(Overlay overlay);

    @Accessor("smarterContraptionStorage$overlay")
    Overlay getOverlay();

    @Mixin(ContraptionControlsBlockEntity.class)
    class ContraptionControlsBlockEntityMixin_ {
        @Shadow(remap = false)
        public FilteringBehaviour filtering;
        @Unique
        @Nullable
        Overlay smarterContraptionStorage$overlay = null;

        @Inject(method = "addBehaviours", at = @At("RETURN"), remap = false)
        public void withPredicate(List<BlockEntityBehaviour> behaviours, CallbackInfo ci) {
            this.filtering.withPredicate((itemStack) -> AllTags.AllItemTags.CONTRAPTION_CONTROLLED.matches(itemStack) || Utils.canBeControlledItem(itemStack.getItem()));
        }

        @Inject(method = "read", at = @At("HEAD"), remap = false)
        protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
            this.smarterContraptionStorage$overlay = Overlay.get(tag.getString("overlay"));
        }

        @Inject(method = "write", at = @At("HEAD"), remap = false)
        protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
            if (smarterContraptionStorage$overlay != null)
                tag.putString("overlay", this.smarterContraptionStorage$overlay.getName());
        }
    }
}
