package org.bukkit.command.defaults;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.StringUtil;

import java.util.Arrays;
import java.util.List;

public class VersionCommand extends BukkitCommand {
    public VersionCommand(String name) {
        super(name);

        this.description = "Gets the version of this server including any plugins in use";
        this.usageMessage = "/version [plugin name]";
        this.setPermission("bukkit.command.version");
        this.setAliases(Arrays.asList("ver", "about"));
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        if (args.length == 0) {
            sendServerVersion(sender);
        } else {
            StringBuilder name = new StringBuilder();

            for (String arg : args) {
                if (name.length() > 0) {
                    name.append(' ');
                }

                name.append(arg);
            }

            String pluginName = name.toString();
            Plugin exactPlugin = Bukkit.getPluginManager().getPlugin(pluginName);
            if (exactPlugin != null) {
                describeToSender(exactPlugin, sender);
            } else {
                boolean found = false;
                pluginName = pluginName.toLowerCase();
                for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                    if (plugin.getName().toLowerCase().contains(pluginName)) {
                        describeToSender(plugin, sender);
                        found = true;
                    }
                }

                if (!found) {
                    sender.sendMessage("This server is not running any plugin by that name.");
                    sender.sendMessage("Use /plugins to get a list of plugins.");
                }
            }
        }
        return true;
    }

    private void describeToSender(Plugin plugin, CommandSender sender) {
        PluginDescriptionFile desc = plugin.getDescription();
        sender.sendMessage(ChatColor.GREEN + desc.getName() + ChatColor.WHITE + " version " + ChatColor.GREEN + desc.getVersion());

        if (desc.getDescription() != null) {
            sender.sendMessage(desc.getDescription());
        }

        if (desc.getWebsite() != null) {
            sender.sendMessage("Website: " + ChatColor.GREEN + desc.getWebsite());
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = Arrays.asList("plugins"); // Default completion option

            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                String name = plugin.getName();
                if (StringUtil.startsWithIgnoreCase(name, args[0])) {
                    completions.add(name);
                }
            }

            return completions;
        }
        return Arrays.asList("plugins"); // Default completion option
    }

    private void sendServerVersion(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_PURPLE + "------------------------------");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "This server is Running" + ChatColor.GOLD + " FulfillSpigot");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Developed by" + ChatColor.GOLD + " Exile Team");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Version: " + ChatColor.YELLOW + "1.8.0"); //TODO: dont need an actual version check its fine to do it directly in the string.
        sender.sendMessage(ChatColor.DARK_PURPLE + "------------------------------");
    }
}
