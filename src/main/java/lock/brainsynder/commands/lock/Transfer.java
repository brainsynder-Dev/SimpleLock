package lock.brainsynder.commands.lock;

import lock.brainsynder.Core;
import lock.brainsynder.commands.api.SubCommand;
import lock.brainsynder.commands.api.annotations.ICommand;
import lock.brainsynder.storage.Config;
import lock.brainsynder.utils.ProtectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@ICommand(
        name = "transfer",
        usage = "&3[&b~&3] &7/simplelock transfer &b<radius>",
        description = "Transfers Lockette/Deadbolt signs to SimpleLock signs in the given radius (performance reasons).")
public class Transfer extends SubCommand {

    private Core core;

    public Transfer(Core core) {
        this.core = core;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }
        if (!args[0].matches("^[0-9]+$")) {
            sendUsage(sender);
            return;
        }
        Player p = (Player) sender;
        sender.sendMessage(core.getConfiguration().getString(Config.TRANSFER_START, true));
        int signs = 0;
        try {
            for (Block b : getNearbyBlocks(p.getLocation(), Integer.parseInt(args[0]))) {
                if (b.getType() == Material.WALL_SIGN) {
                    Sign sign = (Sign) b.getState();
                    if (sign.getLine(0).equalsIgnoreCase("[Private]") ||
                            sign.getLine(0).equalsIgnoreCase("[More users]")) {
                        for (int i = 1; i < 4; i++) {
                            if (sign.getLine(i).isEmpty()) continue;
                            OfflinePlayer op = Bukkit.getOfflinePlayer(sign.getLine(i));
                            if (op != null) {
                                if (!ProtectionUtils.isProtectionSign(sign.getBlock())) {
                                    ProtectionUtils.registerProtection(op, sign.getBlock());
                                    signs++;
                                }
                            }
                        }
                    }
                }
            }
        } catch (OutOfMemoryError e) { // Because of the ridiculous numbers people can put in...
            sender.sendMessage(core.getConfiguration().getString(Config.TRANSFER_NO_MEMORY, true));
        }

        if (signs == 0) {
            sender.sendMessage(core.getConfiguration().getString(Config.NONE_TRANSFERRED, true));
        } else {
            sender.sendMessage(core.getConfiguration().getString(Config.TRANSFER_SUCCESS, true).replaceAll("\\{number}", String.valueOf(signs)));
        }

    }

    // Modified utility used in separate plugin
    public static List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<>();
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = 0; y <= location.getWorld().getMaxHeight(); y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }
}
