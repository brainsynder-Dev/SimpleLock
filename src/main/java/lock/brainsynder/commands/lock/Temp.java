package lock.brainsynder.commands.lock;

import lock.brainsynder.Core;
import lock.brainsynder.commands.api.SubCommand;
import lock.brainsynder.commands.api.annotations.ICommand;
import lock.brainsynder.storage.Config;
import lock.brainsynder.storage.ProtectionData;
import lock.brainsynder.utils.ProtectionUtils;
import lock.brainsynder.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

@ICommand(
        name = "temp",
        usage = "&3[&b~&3] &7/simplelock temp &b<player> <seconds>",
        description = "Adds the player temporarily for the selected seconds"
)
public class Temp extends SubCommand {
    private Core core;

    public Temp (Core core) {
        this.core = core;
        registerCompletion(1, Arrays.asList("5", "10", "40", "60", "120"));
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }
        String name = args[0];
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        Block block = Utilities.getBlock(player);
        if (!ProtectionUtils.isProtectionSign(block)) {
            player.sendMessage(core.getConfiguration().getString(Config.NOT_PROTECTION_SIGN, true));
            return;
        }


        OfflinePlayer offline = Bukkit.getPlayer(name);
        if ((offline == null) || (!offline.isOnline())) {
            offline = null;
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                if (offlinePlayer.getName().equalsIgnoreCase(name)) {
                    offline = offlinePlayer;
                    break;
                }
            }
        }

        if (offline == null) {
            player.sendMessage(core.getConfiguration().getString(Config.PLAYER_NOT_FOUND, true).replace("{user}", name));
            return;
        }

        if (args.length == 1) {
            sendUsage(sender);
            return;
        }
        int seconds = 0;
        try {
            seconds = Integer.parseInt(args[1]);
        }catch (NumberFormatException e){
            return;
        }

        Sign sign = (Sign) block.getState();
        ProtectionData data = ProtectionUtils.getProtectionInfo(sign);
        if (!data.addTemporary(offline, seconds)) {
            player.sendMessage(core.getConfiguration().getString(Config.PLAYER_ALREADY_TEMP, true).replace("{user}", name).replace("{seconds}", ""+seconds));
        }else{
            player.sendMessage(core.getConfiguration().getString(Config.PLAYER_TEMP, true).replace("{user}", name).replace("{seconds}", ""+seconds));
        }
    }
}
