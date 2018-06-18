package lock.brainsynder.commands.lock;

import lock.brainsynder.Core;
import lock.brainsynder.api.IProtection;
import lock.brainsynder.api.TimeInfo;
import lock.brainsynder.commands.api.SubCommand;
import lock.brainsynder.commands.api.annotations.ICommand;
import lock.brainsynder.storage.ConfigValues;
import lock.brainsynder.utils.ProtectionUtils;
import lock.brainsynder.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
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
            player.sendMessage(core.getConfiguration().getString(ConfigValues.NOT_PROTECTED, true));
            return;
        }

        Sign sign = (Sign) block.getState();
        IProtection data = ProtectionUtils.getProtectionInfo(sign);

        player.sendMessage(core.getConfiguration().getString(ConfigValues.PLAYER_INFO_OWNER, true).replace("{user}", data.getOwnerName()));
        player.sendMessage(core.getConfiguration().getString(ConfigValues.PLAYER_INFO_ALLOWED, true).replace("{value}", ""+data.isPlayerAllowed(player)));
        if (!data.getTrusted().isEmpty()) {
            player.sendMessage(core.getConfiguration().getString(ConfigValues.PLAYER_INFO_TRUSTED_PREFIX, true));
            data.getTrusted().keySet().forEach(name -> {
                player.sendMessage(core.getConfiguration().getString(ConfigValues.PLAYER_INFO_TRUSTED_LIST, true).replace("{user}", name));

            });
        }
        if (!data.getAdded().isEmpty()) {
            player.sendMessage(core.getConfiguration().getString(ConfigValues.PLAYER_INFO_ADDED_PREFIX, true));
            data.getAdded().keySet().forEach(name -> {
                player.sendMessage(core.getConfiguration().getString(ConfigValues.PLAYER_INFO_ADDED_LIST, true).replace("{user}", name));
            });
        }
        if (!data.getTempAdded().isEmpty()) {
            boolean sent = false;
            for (Map.Entry<String, TimeInfo> entry : data.getTempAdded().entrySet()) {
                String uuid = entry.getKey();
                TimeInfo info = entry.getValue();
                if (!info.hasTimeRemaining()) return;
                if (!sent) {
                    player.sendMessage(core.getConfiguration().getString(ConfigValues.PLAYER_INFO_TEMP_PREFIX, true));
                    sent = true;
                }

                OfflinePlayer offline = Bukkit.getPlayer(UUID.fromString(uuid));
                if ((offline == null) || (!offline.isOnline())) {
                    for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                        if (offlinePlayer.getUniqueId().toString().equalsIgnoreCase(uuid)) {
                            offline = offlinePlayer;
                            break;
                        }
                    }
                }
                player.sendMessage(core.getConfiguration().getString(ConfigValues.PLAYER_INFO_TEMP_LIST, true).replace("{user}", offline.getName()).replace("{seconds}", ""+info.getRemainingTime()));
            }
        }
    }
}
