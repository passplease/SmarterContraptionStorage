package Excludes;

import net.minecraft.nbt.CompoundTag;

class storage_control extends CreateNBTFile{
    public storage_control(String name) {
        super(name);
    }

    @Override
    public int setValue() {
        addSize(7);
        addSize(3);
        addSize(6);
        addPalette("111");
        addBlock(1,1,1,"111");
        addBlock(2,2,2,"222",null);
        return 1;
    }
}