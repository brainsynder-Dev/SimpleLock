package lock.brainsynder.commands;

import lock.brainsynder.Core;
import lock.brainsynder.commands.api.ParentCommand;
import lock.brainsynder.commands.api.annotations.ICommand;
import lock.brainsynder.commands.lock.*;
import org.bukkit.command.CommandSender;

@ICommand(
        name = "simplelock",
        alias = {"sl"}
)
public class SimpleLockCommand extends ParentCommand {
    public SimpleLockCommand (Core core) {
        registerSub(new Add(core));
        registerSub(new Trust(core));
        registerSub(new Temp(core));
        registerSub(new Info(core));
        registerSub(new Remove(core));
        registerSub(new Transfer(core));
    }

    @Override
    public void run(CommandSender sender) {
        sendHelp(sender, false);
    }
}
