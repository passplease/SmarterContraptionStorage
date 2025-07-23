package net.smartercontraptionstorage.Mixin.RS;

import com.refinedmods.refinedstorage.common.support.network.AbstractBaseNetworkNodeContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractBaseNetworkNodeContainerBlockEntity.class)
public interface AbstractBaseNetworkNodeContainerBlockEntityMixin {
    @Invoker("calculateActive")
    boolean isActive();
}
