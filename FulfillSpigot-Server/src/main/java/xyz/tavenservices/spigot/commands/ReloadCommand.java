package xyz.tavenservices.spigot.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.tavenservices.spigot.FulfillSpigot;

public class ReloadCommand extends Command {

    public ReloadCommand(String name) {
        super(name);
        this.setPermission("fulfillspigot.command.reload");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This may only be used by Players");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("fulfillspigot.command.reload")) {
            player.sendMessage("No permission.");
            return true;
        }

        FulfillSpigot.getInstance().reloadKnockbackConfig();
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Knockback configuration reloaded successfully.");
        return true;
    }
}
