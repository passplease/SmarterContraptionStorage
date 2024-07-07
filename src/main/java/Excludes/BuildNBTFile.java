package Excludes;

import Excludes.Scenes.replenish_item;
import Excludes.Scenes.storage_control;
import Excludes.Scenes.trash_control;
import net.smartercontraptionstorage.SmarterContraptionStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class BuildNBTFile {
    private static int num = 1;
    private static final String Path = Paths.get("").toAbsolutePath().toString()
            .replace("run","src/main/resources/assets/" + SmarterContraptionStorage.MODID + "/ponder/")
            .replace('\\','/');
    private static final Map<Integer, CreateNBTFile> map = new HashMap<>();
    static {
        map.put(map.size(),new storage_control("storage_control"));
        map.put(map.size(),new trash_control("trash_control"));
        map.put(map.size(),new replenish_item("replenish_item"));
    }
    public static void createNBTFile() {
        String path;
        for(int i = map.size() - 1;i >= 0; i--) {
            path = map.get(i).Name + ".nbt";
            createNBTFile(Path + path, map.get(i));
        }
    }
    private static void createNBTFile(String path, CreateNBTFile writeToCompoundTag){
        CompoundTag nbt = new CompoundTag();
        nbt = writeToCompoundTag.function(nbt,path,num);
        File file = new File(path);
        try(OutputStream stream = new FileOutputStream(file)){
            NbtIo.writeCompressed(nbt,stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        num++;
    }
}