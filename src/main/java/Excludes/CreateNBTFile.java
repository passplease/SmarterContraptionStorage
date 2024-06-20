package Excludes;

import net.SmarterContraptionStorage.FunctionInterface.TriFunction;
import net.minecraft.nbt.*;
import org.jetbrains.annotations.Nullable;

abstract class CreateNBTFile implements TriFunction<CompoundTag,String, Integer,CompoundTag> {
    String Name;
    public CreateNBTFile(String name){
        Name = name;
    }

    @Override
    public final CompoundTag function(CompoundTag tag, String string, Integer integer) {
        int DataVersion = setValue();
        tag.put("size",s);
        tag.put("entities",e);
        tag.put("blocks",b);
        tag.put("palette",p);
        tag.put("DataVersion",IntTag.valueOf(DataVersion));
        return tag;
    }
    public abstract int setValue();// return DataVersion

    static final ListTag s = new ListTag();
    public static void addSize(int value){
        s.add(IntTag.valueOf(value));
    }
    static final ListTag e = new ListTag();
    @Deprecated
    // Don't know how to write this function yet.
    public static void addEntity(){}
    static final ListTag b = new ListTag();
    public static void addBlock(int x,int y,int z,String blockId){
        addBlock(x,y,z,blockId,null);
    }
    public static void addBlock(int x,int y,int z,String blockId,@Nullable CompoundTag properties){
        CompoundTag tag = new CompoundTag();
        ListTag pos = new ListTag();
        pos.add(IntTag.valueOf(x));
        pos.add(IntTag.valueOf(y));
        pos.add(IntTag.valueOf(z));
        tag.put("pos",pos);
        int id = -1;
        if(properties != null)
            addPalette(blockId,properties);
        for (int i = 0; i < p.size(); i++) {
            if(p.get(i) instanceof CompoundTag){
                if(((CompoundTag) p.get(i)).getString("Name").endsWith(blockId)) {
                    id = i;
                    break;
                }
            }
        }
        if(id == -1){
            addPalette(blockId,properties);
            id = p.size();
        }
        tag.put("state",IntTag.valueOf(id));
        b.add(tag);
    }
    static final ListTag p = new ListTag();
    public static void addPalette(String blockId){
        addPalette(blockId,null);
    }
    public static void addPalette(String blockId,@Nullable CompoundTag properties){
        CompoundTag tag = new CompoundTag();
        if(properties != null)
            tag.put("Properties",properties);
        tag.put("Name",StringTag.valueOf(blockId));
        p.add(tag);
    }
}