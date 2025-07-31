package net.smartercontraptionstorage.Render;

import net.minecraft.world.item.DyeItem;
import org.jetbrains.annotations.Nullable;

public interface OverlayHolder {
    Overlay getOverlay();

    void setOverlay(@Nullable Overlay overlay);

    void setOverlay(@Nullable DyeItem dye);
}
