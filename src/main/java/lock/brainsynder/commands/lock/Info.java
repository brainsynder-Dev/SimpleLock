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

import java.util.UUID;

@ICommand(
        name = "info",
        usage = "&3[&b~&3] &7/simplelock info",
        description = "Lists information about the protection sign you are looking at"
)
public class Info extends SubCommand {
    private Core core;

    public Info (Core core) {
        this.core = core;
    }

    @Override
    public void run(CommandSender sender) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        Block block = Utilities.getBlock(player);
        if (!ProtectionUtils.isProtectionSign(block)) {
            player.sendMessage(core.getConfiguration().getString(Config.NOT_PROTECTION_SIGN, true));
            return;
        }

        Sign sign = (Sign) block.getState();
        ProtectionData data = ProtectionUtils.getProtectionInfo(sign);

        player.sendMessage("Owner: " + data.getOwnerName());
        player.sendMessage("Are You Allowed: " + data.isPlayerAllowed(player));
        if (!data.getTrusted().isEmpty()) {
            player.sendMessage("Trusted: ");
            data.getTrusted().keySet().forEach(name -> {
                player.sendMessage("  - "+name);

            });
        }
        if (!data.getAdded().isEmpty()) {
            player.sendMessage("Added: ");
            data.getAdded().keySet().forEach(name -> {
                player.sendMessage("  - "+name);

            });
        }
        if (!data.getTempAdded().isEmpty()) {
            player.sendMessage("Temp: ");
            data.getTempAdded().forEach((uuid, info) -> {
                if (!info.hasTimeRemaining()) return;
                OfflinePlayer offline = Bukkit.getPlayer(UUID.fromString(uuid));
                if ((offline == null) || (!offline.isOnline())) {
                    for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                        if (offlinePlayer.getUniqueId().toString().equalsIgnoreCase(uuid)) {
                            offline = offlinePlayer;
                            break;
                        }
                    }
                }
                player.sendMessage("  - "+offline.getName()+" | Remaining: " + info.getRemainingTime());
            });
        }
    }
}
