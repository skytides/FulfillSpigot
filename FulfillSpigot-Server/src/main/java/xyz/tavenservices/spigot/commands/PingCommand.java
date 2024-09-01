package xyz.tavenservices.spigot.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand extends Command {

    public PingCommand(String name) {
        super(name);
        this.description = "Shows a player's ping";
        this.setPermission("fulfillspigot.command.ping");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This may only be used by Players");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            int ping = player.spigot().getPing();
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Your ping: " + ping + " ms.");
        } else if (args.length == 1) {
            Player targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer != null) {
                int targetPing = targetPlayer.spigot().getPing();
                player.sendMessage(ChatColor.LIGHT_PURPLE + targetPlayer.getName() + "'s ping: " + targetPing + " ms.");
            } else {
                player.sendMessage(ChatColor.RED + "That player is not online.");
            }
        }

        return true;
    }
}
