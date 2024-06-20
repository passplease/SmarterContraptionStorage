package Excludes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;

class storage_control extends CreateNBTFile{
    public storage_control(String name) {
        super(name);
    }

    @Override
    public int setValue() {
        addSize(7);
        addSize(3);
        addSize(6);
        setBasePlate(7,7);
        CompoundTag properties = new CompoundTag();
        properties.put("facing", StringTag.valueOf("south"));
        addBlock(7,-1,7,"create:creative_motor",properties);
        addBlock(7,0,8,"create:large_cogwheel",properties);
        addBlock(7,-1,8,"create:cogwheel",properties);
        addBlock(7,1,8,"create:cogwheel",properties);
        addBlock(7,1,7,"create:clutch",properties);
        addBlock(7,2,7,"minecraft:lever");
        properties.put("facing", StringTag.valueOf("west"));
        addBlock(5,1,2,"create:mechanical_saw",properties);
        addBlock(5,1,3,"create:mechanical_saw");
        addBlock(6,1,5,"create:sticky_mechanical_piston",properties);
        properties = new CompoundTag();
        properties.put("facing",StringTag.valueOf("north"));
        addBlock(5,1,1,"storagedrawers:oak_full_drawers_4",properties);
        return 1;
    }
}