package lock.brainsynder;

import lock.brainsynder.listeners.InteractListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new InteractListener(this), this);
    }
}
