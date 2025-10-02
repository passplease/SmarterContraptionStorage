package net.smartercontraptionstorage.AddStorage.FluidHander;

import com.mojang.serialization.*;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.smartercontraptionstorage.AddStorage.ItemHandler.MovingItemStorageType;
import net.smartercontraptionstorage.FunctionChanger;
import net.smartercontraptionstorage.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

public class MovingFluidStorageType extends MountedFluidStorageType<MovingFluidStorage> {
    public static final String TYPE = "MovingFluidStorageType";
    public static final RegistryEntry<MountedFluidStorageType<?>,MovingFluidStorageType> HELPER_STORAGE = MovingItemStorageType.REGISTRATE.mountedFluidStorage("helper_fluid_storage",MovingFluidStorageType::new).register();
    
    public static final MapCodec<MovingFluidStorage> CODEC = new MapCodec<>() {
        @Override
        public <T> DataResult<MovingFluidStorage> decode(DynamicOps<T> ops, MapLike<T> input) {
            if(ops.convertTo(NbtOps.INSTANCE, input.get(TYPE)) instanceof CompoundTag nbt) {
                FluidHandlerHelper helper = FluidHandlerHelper.findByName(nbt.getString("helper"));
                try {
                    IFluidHandler handler = null;
                    BlockEntity blockEntity = FunctionChanger.getBlockEntity(NbtUtils.readBlockPos(nbt,MovingItemStorageType.TAG).orElseThrow());
                    if(helper.canDeserialize() && blockEntity.getLevel() != null) {
                        handler = helper.deserialize(nbt,blockEntity.getLevel().registryAccess(),blockEntity.getLevel().isClientSide());
                    }else if(helper.canCreateHandler(blockEntity)){
                        handler = helper.createHandler(blockEntity);
                    }
                    MovingFluidStorage storage = new MovingFluidStorage(handler, helper, blockEntity);
                    storage.blockEntity = blockEntity;
                    return DataResult.success(storage);
                } catch (IllegalAccessException ignored) {
                    Utils.addError("Helper cannot create handler : " + helper.getName());
                    return DataResult.success(null);
                }
            }else return DataResult.error(() -> "Can not convert to CompoundTag ! Decode Failed !");
        }

        @Override
        public <T> RecordBuilder<T> encode(MovingFluidStorage input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            CompoundTag nbt;
            Objects.requireNonNull(input.blockEntity);
            if(input.helper.canDeserialize() && input.blockEntity.getLevel() != null) {
                nbt = input.getHelper().serializeNBT(input.blockEntity.getLevel().registryAccess(), input.getHandler());
            }else {
                input.helper.addStorageToWorld(input.blockEntity, input.getHandler());
                nbt = new CompoundTag();
            }
            nbt.putString("helper",input.helper.getName());
            return prefix.add(TYPE,NbtOps.INSTANCE.convertTo(ops, nbt));
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.empty();
        }
    };
    
    protected MovingFluidStorageType() {
        super(CODEC);
    }

    @Override
    public @Nullable MovingFluidStorage mount(Level level, BlockState blockState, BlockPos blockPos, @Nullable BlockEntity blockEntity) {
        FluidHandlerHelper helper = FluidHandlerHelper.findSuitableHelper(blockEntity);
        if(helper == null)
            return null;
        return new MovingFluidStorage(helper.createHandler(blockEntity), helper, blockEntity);
    }

    public static void load(){}

    public static void register(){
        FluidHandlerHelper.getHandlerHelpers().forEach(handlerHelper -> handlerHelper.registerBlock(MovingFluidStorageType::register));
    }

    private static void register(Block block){
        MovingFluidStorageType.REGISTRY.register(block, HELPER_STORAGE.get());
    }
}
