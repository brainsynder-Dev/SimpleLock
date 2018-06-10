package lock.brainsynder.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import simple.brainsynder.storage.IStorage;
import simple.brainsynder.storage.StorageList;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Config {
    public static String ALLOWED_BLOCKS = "allowed-blocks",
            PLAYER_NOT_FOUND = "message.player-not-found",
            PLAYER_ADDED = "message.add-player",
            PLAYER_ALREADY_ADDED = "message.player-already-added",
            PLAYER_TRUSTED = "message.player-trusted",
            PLAYER_ALREADY_TRUSTED = "message.player-already-trusted",
            PLAYER_TEMP = "message.player-temp-added",
            PLAYER_ALREADY_TEMP = "message.player-already-temp",
            PLAYER_REMOVED = "message.player-removed",
            COULD_NOT_REMOVE = "message.cant-remove-player",
            NOT_PROTECTION_SIGN = "message.not-protection-sign";

    private File file;
    private FileConfiguration configuration;

    public Config(Plugin plugin, String fileName) {
        file = new File(plugin.getDataFolder(), fileName);
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        if (!file.exists()) try {
            file.createNewFile();
        } catch (IOException ignored) {
        }
        this.configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void setDefault(String key, Object value) {
        if (!isSet(key)) set(key, value);
    }

    public String getString(String tag, boolean color) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        String value = configuration.getString(tag);
        if (value == null) return "";
        return (color ? translate(value) : value);
    }

    public String getString(String tag) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return this.configuration.get(tag) != null ? this.configuration.getString(tag) : tag;
    }

    public ItemStack getItemStack(String tag) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return this.configuration.getItemStack(tag);
    }

    public boolean getBoolean(String tag) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return this.configuration.get(tag) != null && this.configuration.getBoolean(tag);
    }

    public int getInt(String tag) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return this.configuration.get(tag) != null ? this.configuration.getInt(tag) : 0;
    }

    public double getDouble(String tag) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return this.configuration.get(tag) != null ? this.configuration.getDouble(tag) : 0.0D;
    }

    public Set<String> getKeys(boolean tag) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return this.configuration.getKeys(tag);
    }

    public List<String> getStringList(String tag) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return (List) (this.isSet(tag) ? this.configuration.getStringList(tag) : new ArrayList());
    }

    public IStorage<String> getStorageList(String tag) {
        return (IStorage<String>) new StorageList(this.getStringList(tag));
    }

    public boolean isSet(String tag) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return this.configuration.get(tag) != null;
    }

    public ConfigurationSection getSection(String tag) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return this.configuration.getConfigurationSection(tag);
    }

    public Object get(String tag) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return this.configuration.get(tag);
    }

    private String translate(String msg) {
        return msg.replace("&", "§");
    }

    public void set(String tag, Object data) {
        configuration.set(tag, data);
        try {
            configuration.save(file);
            //this.simpleConfig.reloadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setHeader(String... header) {
        this.configuration.options().header(Arrays.toString(header));
    }

    public Map<String, Object> getConfigSectionValue(Object o) {
        return this.getConfigSectionValue(o, false);
    }

    public Map<String, Object> getConfigSectionValue(Object o, boolean deep) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        Map<String, Object> map = new HashMap();
        if (o == null) {
            return map;
        } else {
            if (o instanceof ConfigurationSection) {
                map = ((ConfigurationSection) o).getValues(deep);
            } else if (o instanceof Map) {
                map = (Map) o;
            }

            return map;
        }
    }

    public void set(boolean checkNull, String tag, Object data) {
        if (checkNull) {
            if (!this.isSet(tag)) {
                this.set(tag, data);
            }
        } else {
            this.set(tag, data);
        }

    }
}