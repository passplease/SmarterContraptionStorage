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

    private static final ListTag s = new ListTag();
    public static void addSize(int value){
        s.add(IntTag.valueOf(value));
    }
    private static final ListTag e = new ListTag();
    @Deprecated
    // Don't know how to write this function yet.
    public static void addEntity(){}
    private static final ListTag b = new ListTag();
    public static void addBlock(int x,int y,int z,String blockId){
        addBlock(x,y,z,blockId,null);
    }
    public static int addBlock(int x,int y,int z,String blockId,@Nullable CompoundTag properties){
        CompoundTag tag = new CompoundTag();
        ListTag pos = new ListTag();
        pos.add(IntTag.valueOf(x));
        pos.add(IntTag.valueOf(y));
        pos.add(IntTag.valueOf(z));
        tag.put("pos",pos);
        int id = -1;
        if(properties != null)
            id = addPalette(blockId,properties);
        else for (int i = 0; i < p.size(); i++){
            if(p.get(i) instanceof CompoundTag){
                if(((CompoundTag) p.get(i)).getString("Name").endsWith(blockId)) {
                    id = i;
                    break;
                }
            }
        }
        if(id == -1){
            addPalette(blockId,properties);
            id = p.size() - 1;
        }
        tag.put("state",IntTag.valueOf(id));
        b.add(tag);
        return id;
    }
    public static void addBlock(int x,int y,int z,int id){
        CompoundTag tag = new CompoundTag();
        ListTag pos = new ListTag();
        pos.add(IntTag.valueOf(x));
        pos.add(IntTag.valueOf(y));
        pos.add(IntTag.valueOf(z));
        tag.put("pos",pos);
        tag.put("state",IntTag.valueOf(id));
    }
    private static final ListTag p = new ListTag();
    public static int addPalette(String blockId){
        return addPalette(blockId,null);
    }
    public static int addPalette(String blockId,@Nullable CompoundTag properties){
        CompoundTag tag = new CompoundTag();
        if(properties != null)
            tag.put("Properties",properties);
        tag.put("Name",StringTag.valueOf(blockId));
        p.add(tag);
        return p.size() - 1;
    }
    public static void setBasePlate(int x,int z){
        setBasePlate(new String[]{"minecraft:white_concrete","minecraft:snow_block"},x,z);
    }
    public static void setBasePlate(String[] blockId,int x,int z){
        setBasePlate(blockId,0,0,x,z);
    }
    public static void setBasePlate(String[] blockId,int x,int z,int X,int Z){
        int size = blockId.length;
        int i = 0;
        for(int a = x;a <= X;a++,i = (a - x) % size)
            for (int c = z; c <= Z; c++) {
                addBlock(a,0,c,blockId[i]);
                i++;
                i = i >= size ? i - size : i;
            }
    }
}