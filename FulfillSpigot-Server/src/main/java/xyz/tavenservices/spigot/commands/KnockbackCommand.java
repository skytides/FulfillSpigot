package xyz.tavenservices.spigot.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.tavenservices.spigot.config.KnockbackConfig;
import xyz.tavenservices.spigot.event.sound.KnockbackProfile;
import xyz.tavenservices.spigot.knockback.CraftKnockbackProfile;

import java.util.Arrays;

public class KnockbackCommand extends Command {

    public KnockbackCommand() {
        super("knockback");
        this.setAliases(Arrays.asList("kb"));
        this.setUsage(ChatColor.WHITE + "/knockback view | set <profile>");
        this.setPermission("fulfillspigot.command.knockback");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This may only be used by Players");
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("fulfillspigot.command.knockback")) {
            player.sendMessage("No permission.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return false;
        }

        if (args[0].equalsIgnoreCase("view")) {
            viewCurrentKnockbackValues(player);
        } else if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /knockback set <profile>");
                return false;
            }

            String profileName = args[1];
            setKnockbackProfile(player, profileName);
        } else {
            sendHelp(player);
        }

        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.DARK_PURPLE + "Knockback Command Help:");
        player.sendMessage(ChatColor.LIGHT_PURPLE + " o /knockback (Shows this help menu)");
        player.sendMessage(ChatColor.LIGHT_PURPLE + " o /knockback view (View current knockback profile values)");
        player.sendMessage(ChatColor.LIGHT_PURPLE + " o /knockback set <profile> (Set the current knockback profile)");
        player.sendMessage(ChatColor.LIGHT_PURPLE + " o /kbreload (Reloads the knockback config from knockback.yml)");
    }

    private void viewCurrentKnockbackValues(Player player) {
        CraftKnockbackProfile currentProfile = (CraftKnockbackProfile) KnockbackConfig.getCurrentKb();

        if (currentProfile == null) {
            player.sendMessage(ChatColor.RED + "No current knockback profile is set.");
            return;
        }
        player.sendMessage("");
        player.sendMessage(ChatColor.DARK_PURPLE + "Current Knockback Profile Values:");
        player.sendMessage(ChatColor.LIGHT_PURPLE + " o Current profile: " + ChatColor.WHITE + currentProfile.getName());
        player.sendMessage(ChatColor.LIGHT_PURPLE + " o Vertical Knockback: " + ChatColor.WHITE + currentProfile.getVertical());
        player.sendMessage(ChatColor.LIGHT_PURPLE + " o Horizontal Knockback: " + ChatColor.WHITE + currentProfile.getHorizontal());
        player.sendMessage(ChatColor.LIGHT_PURPLE + " o Range Factor: " + ChatColor.WHITE + currentProfile.getRangeFactor());
        player.sendMessage(ChatColor.LIGHT_PURPLE + " o Max Range Reduction: " + ChatColor.WHITE + currentProfile.getMaxRangeReduction());
        player.sendMessage(ChatColor.LIGHT_PURPLE + " o Start Range Reduction: " + ChatColor.WHITE + currentProfile.getStartRangeReduction());
        player.sendMessage(ChatColor.LIGHT_PURPLE + " o Min Range: " + ChatColor.WHITE + currentProfile.getMinRange());
        player.sendMessage(ChatColor.LIGHT_PURPLE + " o Friction: " + ChatColor.WHITE + currentProfile.getFriction());
        player.sendMessage("");
    }

    private void setKnockbackProfile(Player player, String profileName) {
        KnockbackProfile profile = KnockbackConfig.getKbProfileByName(profileName);

        if (profile == null) {
            player.sendMessage(ChatColor.RED + "Knockback profile '" + profileName + "' does not exist.");
            return;
        }

        KnockbackConfig.setCurrentKb(profile);
        KnockbackConfig.set("knockback.current", profileName);
        player.sendMessage(ChatColor.GREEN + "Knockback profile set to '" + profileName + "'.");
    }
}
