package Excludes;

import net.minecraft.nbt.*;
import net.minecraft.world.level.block.state.properties.PistonType;

class storage_control extends CreateNBTFile{
    public storage_control(String name) {
        super(name);
    }

    @Override
    public int setValue() {
        setBasePlate(4,4);
        CompoundTag properties = new CompoundTag();
        properties.put("waterlogged",StringTag.valueOf("false"));
        properties.put("axis",StringTag.valueOf("z"));
        addStressBlock(3,1,5,"create:cogwheel",properties,32,0);
        addStressBlock(4,0,5,"create:large_cogwheel",properties,-16,0);
        addStressBlock(3,1,3,"create:shaft",properties,-32,0);
        properties.put("powered",StringTag.valueOf("true"));
        addStressBlock(3,1,4,"create:gearshift",properties,-32,0);
        properties.put("face",StringTag.valueOf("floor"));
        properties.put("facing",StringTag.valueOf("east"));
        addBlock(3,2,4,"minecraft:lever",properties);

        properties = new CompoundTag();
        properties.put("facing",StringTag.valueOf("west"));
        properties.put("axis_along_first",StringTag.valueOf("false"));
        properties.put("state",StringTag.valueOf("moving"));
        addStressBlock(3,1,2,"create:sticky_mechanical_piston",properties,-32,4);

        properties = new CompoundTag();
        properties.put("facing",StringTag.valueOf("up"));
        int id = addBlock(1,1,2,"minecraft:dark_oak_log",properties);
        addBlock(1,1,3,id);
        addBlock(0,1,2,id);
        addBlock(0,1,3,id);
        properties.put("facing",StringTag.valueOf("north"));
        addBlock(2,1,0,"minecraft:chest",properties);
        CompoundTag nbt = new CompoundTag();
        nbt.put("NextTick",IntTag.valueOf(1));
        nbt.put("Speed",FloatTag.valueOf(0));
        nbt.put("Progress",IntTag.valueOf(0));
        nbt.put("NeedSpeedUpdate", ByteTag.valueOf((byte) 1));
        nbt.put("id",StringTag.valueOf("create:mechanical_saw"));
        properties.put("facing",StringTag.valueOf("west"));
        id = addBlock(2,1,1,"create:mechanical_saw",properties,nbt);
        addBlock(2,1,2,id,nbt);
        properties.put("waterlogged",StringTag.valueOf("false"));
        id = addBlock(4,1,1,"create:piston_extension_pole",properties);
        addBlock(5,1,1,id);
        properties.put("facing",StringTag.valueOf("north"));
        properties.put("virtual",StringTag.valueOf("false"));
        properties.put("open",StringTag.valueOf("false"));
        addBlock(3,1,0,"create:contraption_controls",properties);

        properties = new CompoundTag();
        properties.put("facing",StringTag.valueOf("west"));
        properties.put("waterlogged",StringTag.valueOf("false"));
        properties.put("type",StringTag.valueOf(PistonType.STICKY.name()));
        addBlock(3,1,1,"create:mechanical_piston_head",properties);
        return 1;
    }
}