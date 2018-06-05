package lock.brainsynder.commands.api;

import lock.brainsynder.commands.api.annotations.ICommand;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.*;

public class SubCommand {
    private Map<Integer, List<String>> tabCompletion = new HashMap<>();

    public void run(CommandSender sender) {
        run(sender, new String[0]);
    }

    public void run(CommandSender sender, String[] args) {
        run(sender);
    }

    protected void registerCompletion (int length, List<String> replacements) {
        Validate.notNull(replacements, "Arguments cannot be null");
        tabCompletion.put(length, replacements);
    }

    public void sendUsage(CommandSender sender) {
        ICommand command = getCommand(getClass());
        if (command == null) return;
        if (command.usage().isEmpty()) return;
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', command.usage()));
    }

    public ICommand getCommand (Class<?> clazz) {
        if (!clazz.isAnnotationPresent(ICommand.class)) return null;
        return clazz.getAnnotation(ICommand.class);
    }

    public Map<Integer, List<String>> getTabCompletion() {
        return tabCompletion;
    }

    public void tabComplete(List<String> completions, CommandSender sender, String[] args) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        if (!tabCompletion.isEmpty()) {
            int length = args.length;
            if (!tabCompletion.containsKey(length)) return;
            List<String> replacements = tabCompletion.getOrDefault(length, new ArrayList<>());
            String toComplete = args[length - 1].toLowerCase(Locale.ENGLISH);
            for (String command : replacements) {
                if (command.isEmpty()) continue;
                if (StringUtil.startsWithIgnoreCase(command, toComplete)) {
                    completions.add(command);
                }
            }
        }
    }

    public boolean canExecute (CommandSender sender) {
        return true;
    }

    public String messageMaker(String[] args, int start) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }
        return builder.toString().trim();
    }
}
