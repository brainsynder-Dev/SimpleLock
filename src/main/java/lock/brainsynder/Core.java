package lock.brainsynder;

import lock.brainsynder.commands.SimpleLockCommand;
import lock.brainsynder.commands.TestCommand;
import lock.brainsynder.commands.api.CommandRegistry;
import lock.brainsynder.listeners.BlockListener;
import lock.brainsynder.listeners.InteractListener;
import lock.brainsynder.listeners.InventoryListener;
import lock.brainsynder.storage.Config;
import lock.brainsynder.storage.ConfigValues;
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
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);

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
        config.setDefault(ConfigValues.ALLOWED_BLOCKS, Arrays.asList(
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

        config.setDefault(ConfigValues.PLAYER_NOT_FOUND, "&4[&c~&4] &c{user} &7could not be found (correct spelling?).");
        config.setDefault(ConfigValues.NOT_PROTECTED, "&4[&c~&4] &cThat block is not protected.");
        config.setDefault(ConfigValues.TRANSFER_START, "&3[&b~&3] &7Starting transfer...");
        config.setDefault(ConfigValues.TRANSFER_SUCCESSFUL, "&2[&a~&2] &7Block was successfully transferred to the new system!");
        config.setDefault(ConfigValues.TRANSFER_FAILED, "&4[&c~&4] &7Could not find any block to transfer");

        config.setDefault(ConfigValues.PLAYER_ADDED_SUCCESSFUL, "&2[&a~&2] &a{user} &7has been added");
        config.setDefault(ConfigValues.PLAYER_ADDED_EXISTING, "&3[&b~&3] &b{user} &7is already added");
        config.setDefault(ConfigValues.PLAYER_ADDED_FAILED, "&4[&c~&4] &c{user} &7couldn't be added because they own the block");

        config.setDefault(ConfigValues.PLAYER_TEMP_SUCCESSFUL, "&2[&a~&2] &a{user} &7has been temporarily added for &a{seconds} &7second(s)");
        config.setDefault(ConfigValues.PLAYER_TEMP_EXISTING, "&3[&b~&3] &b{user} &7is already temporarily added");
        config.setDefault(ConfigValues.PLAYER_TEMP_FAILED, "&4[&c~&4] &c{user} &7couldn't be temporarily added because they own the block");

        config.setDefault(ConfigValues.PLAYER_TRUSTED_SUCCESSFUL, "&2[&a~&2] &a{user} &7has been trusted");
        config.setDefault(ConfigValues.PLAYER_TRUSTED_EXISTING, "&3[&b~&3] &b{user} &7is already trusted");
        config.setDefault(ConfigValues.PLAYER_TRUSTED_FAILED, "&4[&c~&4] &c{user} &7couldn't be trusted because they own the block");

        config.setDefault(ConfigValues.PLAYER_REMOVE_SUCCESSFUL, "&2[&a~&2] &a{user} &7has been removed");
        config.setDefault(ConfigValues.PLAYER_REMOVE_MISSING, "&4[&c~&4] &c{user} &7isn't added/trusted to this block");
        config.setDefault(ConfigValues.PLAYER_REMOVE_FAILED, "&4[&c~&4] &c{user} &7couldn't be removed because they own the block");

        config.setDefault(ConfigValues.PLAYER_INFO_OWNER, "&7Owner: &b{user}");
        config.setDefault(ConfigValues.PLAYER_INFO_ALLOWED, "&7Allowed: &b{value}");
        config.setDefault(ConfigValues.PLAYER_INFO_ADDED_PREFIX, "&3Added:");
        config.setDefault(ConfigValues.PLAYER_INFO_ADDED_LIST, " &7- &b{user}");
        config.setDefault(ConfigValues.PLAYER_INFO_TRUSTED_PREFIX, "&2Trusted:");
        config.setDefault(ConfigValues.PLAYER_INFO_TRUSTED_LIST, " &7- &a{user}");
        config.setDefault(ConfigValues.PLAYER_INFO_TEMP_PREFIX, "&6Temporary:");
        config.setDefault(ConfigValues.PLAYER_INFO_TEMP_LIST, " &7- &e{user} &7Remaining: &e{seconds}s");

        // Loads the values from the config
        config.getStringList(ConfigValues.ALLOWED_BLOCKS).forEach(value -> {
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
