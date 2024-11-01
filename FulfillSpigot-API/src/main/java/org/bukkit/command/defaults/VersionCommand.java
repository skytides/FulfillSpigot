package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.StringUtil;

public class VersionCommand extends BukkitCommand {

    private final ReentrantLock versionLock = new ReentrantLock();
    private boolean hasVersion = false;
    private String versionMessage = null;
    private final Set versionWaiters = new HashSet();
    private boolean versionTaskStarted = false;
    private long lastCheck = 0L;
    private static final String BRANCH = "version/1.8.8";

    public VersionCommand(String name) {
        super(name);

        this.description = "Gets the version of this server including any plugins in use";
        this.usageMessage = "/version [plugin name]";
        this.setPermission("bukkit.command.version");
        this.setAliases(Arrays.asList("ver", "about"));
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("This server is running " + ChatColor.AQUA +
                    Bukkit.getName() + ChatColor.RESET +
                    " version " + ChatColor.AQUA +
                    Bukkit.getVersion() + ChatColor.WHITE +
                    " (Implementing API version " + ChatColor.AQUA +
                    Bukkit.getBukkitVersion() + ChatColor.WHITE + ")");
        } else {
            StringBuilder name = new StringBuilder();
            String[] var5 = args;
            int var6 = args.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                String arg = var5[var7];
                if (name.length() > 0) {
                    name.append(' ');
                }

                name.append(arg);
            }

            String pluginName = name.toString();
            Plugin exactPlugin = Bukkit.getPluginManager().getPlugin(pluginName);
            if (exactPlugin != null) {
                this.describeToSender(exactPlugin, sender);
                return true;
            }

            boolean found = false;
            pluginName = pluginName.toLowerCase();
            Plugin[] var15 = Bukkit.getPluginManager().getPlugins();
            int var9 = var15.length;

            for (int var10 = 0; var10 < var9; ++var10) {
                Plugin plugin = var15[var10];
                if (plugin.getName().toLowerCase().contains(pluginName)) {
                    this.describeToSender(plugin, sender);
                    found = true;
                }
            }

            if (!found) {
                sender.sendMessage(ChatColor.WHITE + "This server is not running any plugin by that name.");
                sender.sendMessage(ChatColor.WHITE + "Use /plugins to get a list of plugins.");
            }
        }

        return true;
    }

    private void describeToSender(Plugin plugin, CommandSender sender) {
        PluginDescriptionFile desc = plugin.getDescription();
        sender.sendMessage(ChatColor.AQUA + "Name: " + ChatColor.WHITE + desc.getName());
        sender.sendMessage(ChatColor.AQUA + "Version: " + ChatColor.WHITE + desc.getVersion());
        if (desc.getDescription() != null) {
            sender.sendMessage(desc.getDescription());
        }

        if (desc.getWebsite() != null) {
            sender.sendMessage(ChatColor.AQUA + "Website: " + ChatColor.WHITE + desc.getWebsite());
        }

        if (!desc.getAuthors().isEmpty()) {
            if (desc.getAuthors().size() == 1) {
                sender.sendMessage(ChatColor.AQUA + "Author: " + this.getAuthors(desc));
            } else {
                sender.sendMessage(ChatColor.AQUA + "Authors: " + this.getAuthors(desc));
            }
        }

    }

    private String getAuthors(PluginDescriptionFile desc) {
        StringBuilder result = new StringBuilder();
        List authors = desc.getAuthors();

        for (int i = 0; i < authors.size(); ++i) {
            if (result.length() > 0) {
                result.append(ChatColor.WHITE);
                if (i < authors.size() - 1) {
                    result.append(", ");
                } else {
                    result.append(" and ");
                }
            }

            result.append(ChatColor.WHITE);
            result.append((String) authors.get(i));
        }

        return result.toString();
    }

    public List tabComplete(CommandSender sender, String alias, String[] args) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");
        if (args.length == 1) {
            List completions = new ArrayList();
            String toComplete = args[0].toLowerCase();
            Plugin[] var6 = Bukkit.getPluginManager().getPlugins();
            int var7 = var6.length;

            for (int var8 = 0; var8 < var7; ++var8) {
                Plugin plugin = var6[var8];
                if (StringUtil.startsWithIgnoreCase(plugin.getName(), toComplete)) {
                    completions.add(plugin.getName());
                }
            }

            return completions;
        } else {
            return ImmutableList.of();
        }
    }

    private void sendVersion(CommandSender sender) {
        if (this.hasVersion) {
            if (System.currentTimeMillis() - this.lastCheck <= 21600000L) {
                sender.sendMessage(this.versionMessage);
                return;
            }

            this.lastCheck = System.currentTimeMillis();
            this.hasVersion = false;
        }

        this.versionLock.lock();

        try {
            if (this.hasVersion) {
                sender.sendMessage(this.versionMessage);
                return;
            }

            this.versionWaiters.add(sender);
            sender.sendMessage("Checking version, please wait...");
            if (!this.versionTaskStarted) {
                this.versionTaskStarted = true;
                (new Thread(new Runnable() {
                    public void run() {
                        VersionCommand.this.obtainVersion();
                    }
                })).start();
            }
        } finally {
            this.versionLock.unlock();
        }

    }

    private void obtainVersion() {
        this.setVersionMessage("You are running the latest version!");
    }

    private void setVersionMessage(String msg) {
        this.lastCheck = System.currentTimeMillis();
        this.versionMessage = msg;
        this.versionLock.lock();

        try {
            this.hasVersion = true;
            this.versionTaskStarted = false;
            Iterator var2 = this.versionWaiters.iterator();

            while (var2.hasNext()) {
                CommandSender sender = (CommandSender) var2.next();
                sender.sendMessage(this.versionMessage);
            }

            this.versionWaiters.clear();
        } finally {
            this.versionLock.unlock();
        }
    }
}
