package net.smartercontraptionstorage.AddStorage.FluidHander;

import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.supermartijn642.trashcans.TrashCanBlockEntity;
import com.supermartijn642.trashcans.filter.ItemFilter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.smartercontraptionstorage.Utils.getFluidByItem;

public class TrashcanFluidHelper extends FluidHandlerHelper {
    @Override
    public void addStorageToWorld(BlockEntity entity, SmartFluidTank helper) {
        assert canCreateHandler(entity);
    }

    @Override
    public boolean canCreateHandler(Item comparedItem) {
        String name = comparedItem.getDescriptionId();
        return name.startsWith("trashcans.block.item") || name.startsWith("trashcans.block.ultimate");
    }
    @Override
    public boolean canCreateHandler(Block block) {
        return false;
    }
    @Override
    public boolean canCreateHandler(BlockEntity entity) {
        return entity instanceof TrashCanBlockEntity && ((TrashCanBlockEntity)entity).liquids;
    }
    @Override
    public SmartFluidTank createHandler(BlockEntity entity) {
        assert canCreateHandler(entity);
        return new TrashcanHelper((TrashCanBlockEntity) entity, Utils.getInventory());
    }
    public static class TrashcanHelper extends FluidHelper{
        public final boolean whiteOrBlack;
        public final ArrayList<FluidStack> filter;
        public final List<FluidStack> toolboxFluid;
        public TrashcanHelper(TrashCanBlockEntity entity,@Nullable CompoundTag toolboxItem) {
            super(entity.FLUID_HANDLER.getTankCapacity(0));
            whiteOrBlack = entity.liquidFilterWhitelist;
            filter = new ArrayList<>();
            ArrayList<FluidStack> list;
            for(ItemFilter item : entity.liquidFilter)
                if(item != null) {
                    list = getFluidByItem(item.getRepresentingItem());
                    if (list != null)
                        filter.addAll(list);
                }
            this.toolboxFluid = new ArrayList<>();
            if (toolboxItem != null)
                for(ItemStack fluidItem : NBTHelper.readItemList(toolboxItem.getList("Compartments", Tag.TAG_COMPOUND))){
                    list = getFluidByItem(fluidItem);
                    if(list != null)
                        toolboxFluid.addAll(list);
                }
            fluid = FluidStack.EMPTY;
        }
        @Override
        public boolean canFill(FluidStack stack){
            for(FluidStack toolboxFluid : this.toolboxFluid)
                if(stack.isFluidEqual(toolboxFluid))
                    return false;
            for(FluidStack filterFluid : filter)
                if(stack.isFluidEqual(filterFluid))
                    return whiteOrBlack;
            return !whiteOrBlack;
        }
        @Override
        public void setFluid(int amount,FluidStack stack) {}
        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if(canFill(resource))
                return resource.getAmount();
            else return 0;
        }

        @Override
        public int getAmount() {
            return 0;
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return FluidStack.EMPTY;
        }
        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            return FluidStack.EMPTY;
        }

        @Override
        public CompoundTag serializeNBT() {
            return new CompoundTag();
        }
    }
}