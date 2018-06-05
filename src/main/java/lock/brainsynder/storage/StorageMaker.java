package lock.brainsynder.storage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import simple.brainsynder.nbt.CompressedStreamTools;
import simple.brainsynder.nbt.StorageTagCompound;
import simple.brainsynder.utils.Base64Wrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

public class StorageMaker extends StorageTagCompound {
    private File file;
    private StorageTagCompound compound;

    public StorageMaker (File file) {
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {}
        }
        this.compound = new StorageTagCompound();
        try {
            StorageTagCompound compound = CompressedStreamTools.readCompressed(new FileInputStream(file));
            compound.getKeySet().forEach(key -> {
                this.compound.setTag(key, compound.getTag(key));
            });
        } catch (IOException ignored) {}

        this.file = file;
    }

    @Override
    public Set<String> getKeySet() {
        return this.compound.getKeySet();
    }

    public StorageTagCompound getCompound() {
        return compound;
    }

    public void setCompound(StorageTagCompound compound) {
        this.compound = compound;
    }

    public void save () {
        try {
            if (!file.exists()) file.createNewFile();
            CompressedStreamTools.writeCompressed(this.compound, new FileOutputStream(file));
        }catch (Exception ignored){}
    }

    public void setJSONArray (String key, JSONArray array) {
        this.compound.setString(key, Base64Wrapper.encodeString(array.toJSONString()));
    }

    public void setJSONObject (String key, JSONObject json) {
        this.compound.setString(key, Base64Wrapper.encodeString(json.toJSONString()));
    }

    public JSONArray getJSONArray (String key) {
        if (hasKey(key)) {
            try {
                return (JSONArray) JSONValue.parseWithException(Base64Wrapper.decodeString(this.compound.getString(key)));
            } catch (ParseException ignored) {}
        }

        return new JSONArray();
    }

    public JSONObject getJSONObject (String key) {
        if (hasKey(key)) {
            try {
                return (JSONObject) JSONValue.parseWithException(Base64Wrapper.decodeString(this.compound.getString(key)));
            } catch (ParseException ignored) {}
        }

        return new JSONObject();
    }

    /* STATIC */

    public static void setJSONArray (String key, JSONArray array, StorageTagCompound compound) {
        compound.setString(key, Base64Wrapper.encodeString(array.toJSONString()));
    }

    public static JSONArray getJSONArray (StorageTagCompound compound, String key) {
        if (compound.hasKey(key)) {
            try {
                return (JSONArray) JSONValue.parseWithException(Base64Wrapper.decodeString(compound.getString(key)));
            } catch (ParseException ignored) {}
        }

        return new JSONArray();
    }
}
