package lock.brainsynder.commands;

import lock.brainsynder.commands.api.ParentCommand;
import lock.brainsynder.commands.api.annotations.ICommand;
import lock.brainsynder.commands.lock.Add;
import lock.brainsynder.commands.lock.Trust;
import org.bukkit.command.CommandSender;

@ICommand(
        name = "simplelock",
        alias = {"sl"}
)
public class SimpleLockCommand extends ParentCommand {
    public SimpleLockCommand () {
        registerSub(new Add());
        registerSub(new Trust());
    }

    @Override
    public void run(CommandSender sender) {
        sendHelp(sender, false);
    }
}
