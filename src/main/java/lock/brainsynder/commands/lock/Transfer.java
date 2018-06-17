package lock.brainsynder.commands.lock;

import lock.brainsynder.Core;
import lock.brainsynder.api.IProtection;
import lock.brainsynder.commands.api.SubCommand;
import lock.brainsynder.commands.api.annotations.ICommand;
import lock.brainsynder.storage.ConfigValues;
import lock.brainsynder.storage.ProtectionData;
import lock.brainsynder.utils.ProtectionUtils;
import lock.brainsynder.utils.Utilities;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@ICommand(
        name = "transfer",
        usage = "&3[&b~&3] &7/simplelock transfer",
        description = "Transfers Lockette/Deadbolt signs to SimpleLock signs.")
public class Transfer extends SubCommand {

    private Core core;

    public Transfer(Core core) {
        this.core = core;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;
        Block block = Utilities.getBlock(player);

        if ((block == null) || (block.getType() == Material.AIR)) {
            player.sendMessage(core.getConfiguration().getString(ConfigValues.NOT_PROTECTED, true));
            return;
        }

        sender.sendMessage(core.getConfiguration().getString(ConfigValues.TRANSFER_START, true));
        IProtection data = new ProtectionData();
        String owner = "", loc = "";
        for (Sign sign: Utilities.findSigns(new HashMap<>(), block).values()) {
            String[] lines = sign.getLines();
            boolean trusted = false, privateSign = false;
            for (int i=0; i < lines.length; i++) {
                if (i == 0) {
                    if (lines[0].equalsIgnoreCase("[private]")) privateSign = true;
                    if (lines[0].replace(" ", "").equalsIgnoreCase("[moreusers]")) trusted = true;
                }else{
                    String line = lines[i];
                    if ((i == 1) && privateSign) {
                        owner = lines[1];
                        if (owner != null) {
                            if (!owner.isEmpty()) {
                                loc = Utilities.blockLocToString(sign.getLocation());
                                trusted = true;
                            }
                        }
                    }

                    if (i >= 1) {
                        if (privateSign && trusted) {
                            if (i > 1) {
                                if ((line != null) && (!line.isEmpty()))
                                    data.addTrusted(Bukkit.getOfflinePlayer(line));
                            }
                        }else if ((!privateSign) && trusted) {
                            if ((line != null) && (!line.isEmpty()))
                                data.addTrusted(Bukkit.getOfflinePlayer(line));
                        }
                    }
                }
            }
        }

        if ((owner != null) && (!owner.isEmpty())) {
            String finalOwner = owner;
            String finalLoc = loc;
            CompletableFuture.runAsync(() -> {
                try {
                    System.setProperty("http.agent", "Chrome");
                    URL url = new URL("https://v3.minecraftchar.us/profile/?user="+ finalOwner);
                    URLConnection connection = url.openConnection();
                    connection.addRequestProperty("User-Agent", "Mozilla/5.0");
                    connection.addRequestProperty("Content-Encoding", "gzip");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    JSONObject json = (JSONObject) JSONValue.parseWithException(IOUtils.toString(connection.getInputStream()));
                    if (json.containsKey("name") && (json.containsKey("uuid_formatted"))) {
                        String name = String.valueOf(json.get("name"));
                        String uuid = String.valueOf(json.get("uuid_formatted"));
                        Utilities.sync(() ->{
                            data.setOwnerName(name);
                            data.setOwnerUUID(uuid);
                            if ((finalLoc != null) && (!finalLoc.isEmpty())) {
                                sender.sendMessage(core.getConfiguration().getString(ConfigValues.TRANSFER_SUCCESSFUL, true));
                                ProtectionUtils.registerProtection(finalLoc, data, core);
                            }
                        });
                    }
                }catch (Exception e) {
                    Utilities.sync(() -> sender.sendMessage(core.getConfiguration().getString(ConfigValues.TRANSFER_FAILED, true)));
                }
            });
        }else{
            sender.sendMessage(core.getConfiguration().getString(ConfigValues.TRANSFER_FAILED, true));
        }
    }
}
