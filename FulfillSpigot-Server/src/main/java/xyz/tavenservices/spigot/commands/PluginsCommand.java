package xyz.tavenservices.spigot.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import xyz.tavenservices.spigot.config.FulfillSpigotConfig;

import java.util.Collections;

public class PluginsCommand extends Command {

    private static final int PLUGINS_PER_PAGE = FulfillSpigotConfig.get().pluginsPerPage;

    public PluginsCommand(String name) {
        super(name);
        this.setAliases(Collections.singletonList("pl"));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.hasPermission("fulfillspigot.command.plugins")) {
            sender.sendMessage(ChatColor.RED + "No permission :(");
            return true;
        }

        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        int totalPlugins = plugins.length;

        int page = 1;
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
                if (page <= 0) {
                    sender.sendMessage(ChatColor.RED + "Invalid page number.");
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid page number.");
                return true;
            }
        }

        int totalPages = (int) Math.ceil((double) totalPlugins / PLUGINS_PER_PAGE);
        if (page > totalPages) {
            sender.sendMessage(ChatColor.RED + "No Pages left or no plugins installed. Total pages: " + totalPages);
            return true;
        }

        sender.sendMessage(ChatColor.AQUA + "------------------------------");
        sender.sendMessage(ChatColor.AQUA + "Installed Plugins (Page " + page + "/" + totalPages + "):");

        int start = (page - 1) * PLUGINS_PER_PAGE;
        int end = Math.min(start + PLUGINS_PER_PAGE, totalPlugins);

        for (int i = start; i < end; i++) {
            Plugin plugin = plugins[i];
            String status = plugin.isEnabled() ? ChatColor.AQUA + "Enabled" : ChatColor.RED + "Disabled";
            sender.sendMessage(ChatColor.WHITE + plugin.getName() + ChatColor.WHITE + " - " + status);
        }

        sender.sendMessage(ChatColor.AQUA + "------------------------------");

        if (page < totalPages) {
            sender.sendMessage(ChatColor.YELLOW + "Type /plugins " + (page + 1) + " to see the next page.");
        }

        return true;
    }
}
