package org.bukkit.command.defaults;

import java.util.Arrays;
import java.util.TreeMap;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

// PandaSpigot start - Improved plugins command
public class PluginsCommand extends BukkitCommand {
    public PluginsCommand(String name) {
        super(name);
        this.description = "Gets a list of plugins running on the server";
        this.usageMessage = "/plugins";
        this.setPermission("bukkit.command.plugins");
        this.setAliases(Arrays.asList("pl"));
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        if (sender instanceof Player) {
            ((Player) sender).sendMessage(this.getPluginListComponents(sender.hasPermission("bukkit.command.version")));
            return true;
        }

        sender.sendMessage("Plugins " + getPluginList());
        return true;
    }

    private BaseComponent[] getPluginListComponents(boolean versionCommand) {
        TreeMap<String, Plugin> plugins = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            plugins.put(plugin.getDescription().getName(), plugin);
        }

        ComponentBuilder builder = new ComponentBuilder("Plugins (" + plugins.size() + "): ");

        java.util.List<Plugin> values = new java.util.ArrayList<>(plugins.values());
        for (int i = 0; i < values.size(); i++) {
            Plugin plugin = values.get(i);
            PluginDescriptionFile description = plugin.getDescription();

            builder
                .append(description.getName(), ComponentBuilder.FormatRetention.NONE)
                .color(plugin.isEnabled() ? net.md_5.bungee.api.ChatColor.GREEN : net.md_5.bungee.api.ChatColor.RED)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, this.getHover(description)));

            if (versionCommand) {
                builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/version " + description.getName()));
            }

            if (i != plugins.size() - 1) {
                builder.append(", ", ComponentBuilder.FormatRetention.NONE);
            }
        }

        return builder.create();
    }

    private BaseComponent[] getHover(PluginDescriptionFile description) {
        net.md_5.bungee.api.ChatColor mainColor = net.md_5.bungee.api.ChatColor.YELLOW;
        net.md_5.bungee.api.ChatColor secondaryColor = net.md_5.bungee.api.ChatColor.WHITE;

        ComponentBuilder builder = new ComponentBuilder("Name: ").color(mainColor)
            .append(description.getName()).color(secondaryColor)
            .append("\n")
            .append("Version: ").color(mainColor)
            .append(description.getVersion()).color(secondaryColor);

        if (description.getDescription() != null) {
            builder
                .append("\n")
                .append("Description: ").color(mainColor)
                .append(description.getDescription()).color(secondaryColor);
        }

        if (description.getWebsite() != null) {
            builder
                .append("\n")
                .append("Website: ").color(mainColor)
                .append(description.getWebsite()).color(secondaryColor);
        }

        java.util.List<String> authors = description.getAuthors();
        if (authors != null && !authors.isEmpty()) {
            builder
                .append("\n")
                .append("Authors: ").color(mainColor);

            for (int i = 0; i < authors.size(); i++) {
                String author = authors.get(i);

                builder.append(author).color(secondaryColor);

                if (i != authors.size() - 1) {
                    builder.append(", ").color(net.md_5.bungee.api.ChatColor.GRAY);
                }
            }
        }

        return builder.create();
    }

    private String getPluginList() {
        StringBuilder pluginList = new StringBuilder();
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();

        for (Plugin plugin : plugins) {
            if (pluginList.length() > 0) {
                pluginList.append(ChatColor.WHITE);
                pluginList.append(", ");
            }

            pluginList.append(plugin.isEnabled() ? ChatColor.GREEN : ChatColor.RED);
            pluginList.append(plugin.getDescription().getName());
        }

        return "(" + plugins.length + "): " + pluginList.toString();
    }

    @Override
    public java.util.List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return java.util.Collections.emptyList();
    }
}
