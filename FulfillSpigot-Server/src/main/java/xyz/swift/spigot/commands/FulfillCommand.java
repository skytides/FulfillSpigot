package xyz.swift.spigot.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import xyz.swift.spigot.FulfillSpigot;

import java.util.Arrays;

public class FulfillCommand extends Command {

    public FulfillCommand(final String name) {
        super(name);
        this.description = "Displays information about FulfillSpigot";
        this.setAliases(Arrays.asList("fulfill", "fspigot", "ff"));
        this.setPermission("fulfill.command");
    }

    @Override
    public boolean execute(final CommandSender sender, final String commandLabel, final String[] args) {
        if (!sender.hasPermission(this.getPermission())) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        sender.sendMessage(ChatColor.AQUA + "==== " + ChatColor.WHITE + "FulfillSpigot Information" + ChatColor.AQUA + " ====");
        sender.sendMessage(ChatColor.AQUA + "Version: " + ChatColor.WHITE + FulfillSpigot.VERSION);
        sender.sendMessage(ChatColor.AQUA + "Available Commands:");

        sender.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "/ping" + ChatColor.GRAY + " - Check your ping.");
        sender.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "/knockback" + ChatColor.GRAY + " - Knockback related info.");
        sender.sendMessage(ChatColor.AQUA + " - " + ChatColor.WHITE + "/plugins" + ChatColor.GRAY + " - List active plugins.");
        sender.sendMessage(ChatColor.AQUA + "===================================");

        return true;
    }
}
