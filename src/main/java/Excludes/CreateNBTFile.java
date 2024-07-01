package Excludes;

import net.minecraft.world.level.block.Block;
import net.smartercontraptionstorage.FunctionInterface.TriFunction;
import net.minecraft.nbt.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class CreateNBTFile implements TriFunction<CompoundTag,String, Integer,CompoundTag> {
    @NotNull String Name;
    private static final int WRONG_BLOCK_ID = -1;
    public CreateNBTFile(@NotNull String name){
        Name = name;
    }
    public enum Facing{
        north("north"),
        south("south"),
        west("west"),
        east("east"),
        up("up"),
        down("down");
        final StringTag value;
        Facing(String facing){
            this.value = StringTag.valueOf(facing);
        }
        public void setTag(CompoundTag tag){
            tag.put("facing",value);
        }
    }

    @Override
    public final CompoundTag function(CompoundTag tag, String string, Integer integer) {
        int DataVersion = setValue();
        addSize();
        addStressBlock();
        tag.put("size",s);
        tag.put("entities",e);
        tag.put("blocks",b);
        tag.put("palette",p);
        tag.put("DataVersion",IntTag.valueOf(DataVersion));
        return tag;
    }
    public abstract int setValue();// return DataVersion

    private final ListTag s = new ListTag();
    private void addSize(){
        int x = 1000;
        int y = 1000;
        int z = 1000;
        int X = -1000;
        int Y = -1000;
        int Z = -1000;
        for(Tag block : b)
            if(block instanceof CompoundTag)
                if(((CompoundTag) block).get("pos") instanceof ListTag pos){
                    x = Math.min(pos.getInt(0),x);
                    X = Math.max(pos.getInt(0),X);
                    y = Math.min(pos.getInt(1),y);
                    Y = Math.max(pos.getInt(1),Y);
                    z = Math.min(pos.getInt(2),z);
                    Z = Math.max(pos.getInt(2),Z);
                }
        if(x <= X && y <= Y && z <= Z){
            s.add(IntTag.valueOf(X - x + 1));
            s.add(IntTag.valueOf(Y - y + 1));
            s.add(IntTag.valueOf(Z - z + 1));
        }else throw new RuntimeException("The size of ponder is wrong");
    }
    private final ListTag e = new ListTag();
    @Deprecated
    // Don't know how to write this function yet.
    public static void addEntity(){}
    private static final ListTag b = new ListTag();
    public int addBlock(int x,int y,int z,@NotNull String blockId){
        return addBlock(x,y,z,blockId,null,null,null);
    }
    public int addBlock(int x,int y,int z,@NotNull String blockId,@Nullable CompoundTag properties){
        return addBlock(x,y,z,blockId,properties,null,null);
    }
    public int addBlock(int x,int y,int z,@NotNull String blockId,@Nullable CompoundTag properties,@Nullable Tag nbt){
        return addBlock(x,y,z,blockId,properties,"nbt",nbt);
    }
    public int addBlock(int x,int y,int z,@NotNull String blockId,@Nullable CompoundTag properties,String name,@Nullable Tag tag){
        CompoundTag Tag = new CompoundTag();
        int id;
        if(properties != null)
            id = addPalette(blockId,properties);
        else id = findBlockId(blockId);
        if(id == WRONG_BLOCK_ID)
            id = addPalette(blockId,properties);
        if(tag != null)
            Tag.put(name, tag);
        ListTag pos = new ListTag();
        pos.add(IntTag.valueOf(x));
        pos.add(IntTag.valueOf(y));
        pos.add(IntTag.valueOf(z));
        Tag.put("pos",pos);
        Tag.put("state",IntTag.valueOf(id));
        b.add(Tag);
        return id;
    }
    public void addBlock(int x,int y,int z,int id){
        addBlock(x,y,z,id,null,null);
    }
    public void addBlock(int x,int y,int z,int id,@NotNull CompoundTag tag){
        addBlock(x,y,z,id,"nbt",tag);
    }
    public void addBlock(int x,int y,int z,int id,@Nullable String name,@Nullable CompoundTag tag){
        CompoundTag Tag = new CompoundTag();
        ListTag pos = new ListTag();
        pos.add(IntTag.valueOf(x));
        pos.add(IntTag.valueOf(y));
        pos.add(IntTag.valueOf(z));
        Tag.put("pos",pos);
        Tag.put("state",IntTag.valueOf(id));
        if(name != null && tag != null)
            Tag.put(name,tag);
        b.add(Tag);
    }
    public int findBlockId(String blockId){
        for (int i = 0; i < p.size(); i++){
            if(p.get(i) instanceof CompoundTag){
                if(((CompoundTag) p.get(i)).getString("Name").endsWith(blockId))
                    return i;
            }
        }
        return WRONG_BLOCK_ID;
    }
    private final ListTag p = new ListTag();
    public int addPalette(String blockId,@Nullable CompoundTag properties){
        CompoundTag tag = new CompoundTag();
        if(properties != null)
            tag.put("Properties",properties.copy());
        tag.put("Name",StringTag.valueOf(blockId));
        p.add(tag);
        return p.size() - 1;
    }
    public void setBasePlate(int x,int z){
        setBasePlate(new String[]{"minecraft:white_concrete","minecraft:snow_block"},x,z);
    }
    public void setBasePlate(String[] blockId,int x,int z){
        setBasePlate(blockId,0,0,x,z);
    }
    public void setBasePlate(String[] blockId,int x,int z,int X,int Z){
        int size = blockId.length;
        int i = 0;
        int[] id = new int[size];
        for (int j = 0; j < size; j++) {
            id[j] = findBlockId(blockId[j]);
            if(id[j] == WRONG_BLOCK_ID)
                id[j] = addPalette(blockId[j],null);
        }
        for(int a = x;a <= X;a++,i = (a - x) % size)
            for (int c = z; c <= Z; c++) {
                addBlock(a,0,c,id[i]);
                i++;
                i = i >= size ? i - size : i;
            }
    }
    private final Map<StressBlock,CompoundTag> stressBlock = new HashMap<>();
    public void addStressBlock(int x, int y, int z, String blockId,@Nullable CompoundTag properties, float speed, float addedStress){
        addStressBlock(x,y,z,blockId,properties,speed, (byte) 0,addedStress,null);
    }
    public void addStressBlock(int x, int y, int z, String blockId,@Nullable CompoundTag properties, float speed, byte update, float addedStress,@Nullable Function<CompoundTag,CompoundTag> other){
        if(properties != null)
            addPalette(blockId,properties);
        CompoundTag tag = new CompoundTag();
        tag.put("Speed",FloatTag.valueOf(speed));
        tag.put("NeedSpeedUpdate",ByteTag.valueOf(update));
        tag.put("id",StringTag.valueOf(blockId));
        if(other != null)
            other.apply(tag);
        stressBlock.put(new StressBlock(x, y, z, blockId, speed, addedStress),tag);
    }
    private void addStressBlock(){
        CompoundTag netWork = new CompoundTag();
        netWork.put("Capacity",FloatTag.valueOf(262164F));
        netWork.put("size",IntTag.valueOf(stressBlock.size()));
        netWork.put("Id",LongTag.valueOf(-8246336864199L));
        stressBlock.forEach((block,tag)->{
            CompoundTag Network = netWork.copy();
            if(block.addedStress != 0) {
                Network.put("AddedStress", FloatTag.valueOf(block.addedStress));
                Network.put("Stress", FloatTag.valueOf(block.addedStress * block.speed));
            }
            tag.put("Network",Network);
            addBlock(block.x,block.y,block.z,block.blockId,null,tag);
        });
    }
    public static String getBlockId(@NotNull Block block){
        return block.getDescriptionId().replace("block.","").replace('.', ':');
    }
    private static class StressBlock {
        int x;
        int y;
        int z;
        float speed;
        float addedStress;
        String blockId;
        private StressBlock(int x, int y, int z, String blockId, float speed, float addedStress){
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockId = blockId;
            this.speed = speed;
            this.addedStress = addedStress;
        }
    }
}