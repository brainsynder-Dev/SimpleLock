package lock.brainsynder.commands.api;

import lock.brainsynder.commands.api.annotations.ICommand;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import simple.brainsynder.utils.Reflection;

import java.util.*;

public class SubCommand {
    protected boolean tabOfflinePlayer = true;
    private List<Integer> playerComplete = new ArrayList<>();
    protected Map<Integer, List<String>> tabCompletion = new HashMap<>();

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
    protected void registerPlayerCompletion (int length) {
        playerComplete.add(length);
    }

    public void sendUsage(CommandSender sender) {
        ICommand command = getCommand(getClass());
        if (command == null) return;
        if (command.usage().isEmpty()) return;
        String usage = ChatColor.translateAlternateColorCodes('&', command.usage());
        String description = ChatColor.translateAlternateColorCodes('&', command.description());

        if (sender instanceof Player) {
            Reflection.getTellraw(usage).tooltip(ChatColor.GRAY+description).send((Player) sender);
        }else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', command.usage()));
        }
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
        int length = args.length;
        if (playerComplete.contains(length)) {
            fetchPlayers(completions, sender, args);
            return;
        }

        if (!tabCompletion.isEmpty()) {
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

    private void fetchPlayers (List<String> completions, CommandSender sender, String[] args) {
        if (tabOfflinePlayer) {
            int limit = 50;
            String toComplete = "";
            if (args.length >= 0) toComplete = args[args.length - 1];
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                if (limit == 0) break;
                String name = player.getName();
                if (name.equalsIgnoreCase(sender.getName())) continue;
                ChatColor color = ChatColor.GREEN;
                if (!player.isOnline()) color = ChatColor.RED;
                if (StringUtil.startsWithIgnoreCase(name, toComplete)) {
                    limit--;
                    completions.add(color+name+ChatColor.WHITE);
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
