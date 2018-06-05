package lock.brainsynder.commands;

import lock.brainsynder.commands.api.ParentCommand;
import lock.brainsynder.commands.api.annotations.ICommand;
import lock.brainsynder.storage.ProtectionData;
import lock.brainsynder.utils.ProtectionUtils;
import lock.brainsynder.utils.Utilities;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@ICommand(
        name = "signtest",
        alias = {"st"},
        usage = "/signtest <name>",
        description = "Used for testing the protection signs"
)
public class TestCommand extends ParentCommand {
    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
        } else {
            String name = args[0];
            if (!(sender instanceof Player)) return;
            Player player = (Player)sender;
            Block block = Utilities.getBlock(player);

            if (ProtectionUtils.isProtectionSign(block)) {
                Sign sign = (Sign) block.getState();
                ProtectionData data = ProtectionUtils.getProtectionInfo(sign);
                data.setOwnerName(name);
                data.setOwnerUUID(UUID.randomUUID().toString());
                Utilities.updateSign(sign, "[Private]", name);
            }
        }
    }
}
