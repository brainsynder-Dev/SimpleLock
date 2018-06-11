package lock.brainsynder;

import lock.brainsynder.commands.SimpleLockCommand;
import lock.brainsynder.commands.TestCommand;
import lock.brainsynder.commands.api.CommandRegistry;
import lock.brainsynder.listeners.BlockListener;
import lock.brainsynder.listeners.InteractListener;
import lock.brainsynder.storage.Config;
import lock.brainsynder.storage.StorageMaker;
import lock.brainsynder.utils.ProtectionUtils;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Core extends JavaPlugin {
    private List<Material> protectedTypes;
    private StorageMaker storage;
    private Config config;


    @Override
    public void onEnable() {
        config = new Config(this, "config.yml");
        loadDefaults();
        storage = new StorageMaker(new File(getDataFolder(), "SignStorage.stc"));
        ProtectionUtils.loadProtection(this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new InteractListener(this), this);

        // Command registering
        CommandRegistry registry = new CommandRegistry(this);
        try {
            registry.register(new TestCommand());
            registry.register(new SimpleLockCommand(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadDefaults() {
        protectedTypes = new ArrayList<>();
        config.setHeader(
                "~  means contains ",
                "Example '~CHEST' would be ENDER_CHEST, TRAPPED_CHEST, and CHEST"
        );
        config.setDefault(Config.ALLOWED_BLOCKS, Arrays.asList(
                "~CHEST",
                "~DOOR",
                "~FURNACE",
                "BEACON",
                "~GATE",
                "~SHULKER_BOX",
                "ANVIL",
                "~DETECTOR",
                "DROPPER",
                "DISPENSER",
                "ENCHANTMENT_TABLE"
        ));
        config.setDefault(Config.PLAYER_NOT_FOUND, "&3[&b~&3] &c{user} &7could not be found (correct spelling?).");
        config.setDefault(Config.PLAYER_ADDED, "&3[&b~&3] &b{user} &7has been added");
        config.setDefault(Config.PLAYER_ALREADY_ADDED, "&3[&b~&3] &b{user} &7is already added");
        config.setDefault(Config.PLAYER_TEMP, "&3[&b~&3] &b{user} &7has been temporarily added for &b{seconds} &7second(s)");
        config.setDefault(Config.PLAYER_ALREADY_TEMP, "&3[&b~&3] &b{user} &7is already temporarily added");
        config.setDefault(Config.PLAYER_TRUSTED, "&3[&b~&3] &b{user} &7has been trusted");
        config.setDefault(Config.PLAYER_ALREADY_TRUSTED, "&3[&b~&3] &b{user} &7is already trusted");
        config.setDefault(Config.PLAYER_REMOVED, "&3[&b~&3] &b{user} &7has been removed");
        config.setDefault(Config.COULD_NOT_REMOVE, "&3[&b~&3] &7Could not remove &b{user}");
        config.setDefault(Config.NOT_PROTECTION_SIGN, "&3[&b~&3] &cThat block is not a protection sign.");
        config.setDefault(Config.TRANSFER_START, "&3[&b~&3] &7Starting transfer...");
        config.setDefault(Config.TRANSFER_SUCCESS, "&3[&b~&3] &7Signs were transferred to the new system!");

        // Loads the values from the config
        config.getStringList(Config.ALLOWED_BLOCKS).forEach(value -> {
            value = value.toUpperCase();
            if (!value.startsWith("~")) {
                try {
                    protectedTypes.add(Material.valueOf(value));
                } catch (Exception e) {
                    Material material = Material.matchMaterial(value);
                    if (material != null) {
                        protectedTypes.add(material);
                    }else{
                        getLogger().severe("'" + value + "' is not a valid material");
                    }
                }
            } else {
                value = value.replace("~", "");
                for (Material material : Material.values()) {
                    if (material.name().contains(value)) protectedTypes.add(material);
                }
            }
        });
    }

    public List<Material> getProtectedTypes() {
        return protectedTypes;
    }

    public Config getConfiguration() {
        return config;
    }

    @Override
    public void onDisable() {
        ProtectionUtils.saveProtections(this);
    }

    public StorageMaker getStorage() {
        return storage;
    }
}
