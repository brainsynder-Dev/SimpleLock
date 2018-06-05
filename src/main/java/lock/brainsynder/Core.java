package lock.brainsynder;

import lock.brainsynder.commands.SimpleLockCommand;
import lock.brainsynder.commands.TestCommand;
import lock.brainsynder.commands.api.CommandRegistry;
import lock.brainsynder.listeners.BlockListener;
import lock.brainsynder.listeners.InteractListener;
import lock.brainsynder.storage.StorageMaker;
import lock.brainsynder.utils.ProtectionUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Core extends JavaPlugin {
    private StorageMaker storage;


    @Override
    public void onEnable() {
        storage = new StorageMaker(new File(getDataFolder(), "SignStorage.stc"));
        ProtectionUtils.loadProtection(this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new InteractListener(this), this);
        CommandRegistry registry = new CommandRegistry(this);
        try {
            registry.register(new TestCommand());
            registry.register(new SimpleLockCommand());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        ProtectionUtils.saveProtections(this);
    }

    public StorageMaker getStorage() {
        return storage;
    }
}
