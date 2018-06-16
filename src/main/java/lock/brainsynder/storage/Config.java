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

    public void setDefault(ConfigValues cv, Object value) {
        if (!isSet(cv)) set(cv, value);
    }

    public String getString(ConfigValues cv, boolean color) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        String value = configuration.getString(cv.getPath());
        if (value == null) return "";
        return (color ? translate(value) : value);
    }

    public String getString(ConfigValues cv) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return this.configuration.get(cv.getPath()) != null ? this.configuration.getString(cv.getPath()) : cv.getPath();
    }

    public ItemStack getItemStack(ConfigValues cv) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return this.configuration.getItemStack(cv.getPath());
    }

    public boolean getBoolean(ConfigValues cv) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return this.configuration.get(cv.getPath()) != null && this.configuration.getBoolean(cv.getPath());
    }

    public int getInt(ConfigValues cv) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return this.configuration.get(cv.getPath()) != null ? this.configuration.getInt(cv.getPath()) : 0;
    }

    public double getDouble(ConfigValues cv) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return this.configuration.get(cv.getPath()) != null ? this.configuration.getDouble(cv.getPath()) : 0.0D;
    }

    public Set<String> getKeys(boolean tag) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return this.configuration.getKeys(tag);
    }

    public List<String> getStringList(ConfigValues cv) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return (List) (this.isSet(cv) ? this.configuration.getStringList(cv.getPath()) : new ArrayList());
    }

    public IStorage<String> getStorageList(ConfigValues cv) {
        return (IStorage<String>) new StorageList(this.getStringList(cv));
    }

    public boolean isSet(ConfigValues cv) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return this.configuration.get(cv.getPath()) != null;
    }

    public ConfigurationSection getSection(ConfigValues cv) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return this.configuration.getConfigurationSection(cv.getPath());
    }

    public Object get(ConfigValues cv) {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        return this.configuration.get(cv.getPath());
    }

    private String translate(String msg) {
        return msg.replace("&", "§");
    }

    public void set(ConfigValues cv, Object data) {
        configuration.set(cv.getPath(), data);
        try {
            configuration.save(file);
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
        Map<String, Object> map = new HashMap<>();
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
}