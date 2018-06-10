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

@ICommand(
        name = "remove",
        usage = "&3[&b~&3] &7/simplelock remove &b<player>",
        description = "Removes a player from the sign you are looking at"
)
public class Remove extends SubCommand {
    private Core core;

    public Remove (Core core) {
        this.core = core;
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


        Sign sign = (Sign) block.getState();
        ProtectionData data = ProtectionUtils.getProtectionInfo(sign);
        if (!data.remove(offline)) {
            player.sendMessage(core.getConfiguration().getString(Config.COULD_NOT_REMOVE, true).replace("{user}", name));
        }else{
            player.sendMessage(core.getConfiguration().getString(Config.PLAYER_REMOVED, true).replace("{user}", name));
        }
    }
}
