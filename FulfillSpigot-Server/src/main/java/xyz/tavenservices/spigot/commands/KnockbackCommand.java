package xyz.tavenservices.spigot.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.tavenservices.spigot.FulfillSpigot;
import xyz.tavenservices.spigot.config.KnockbackConfig;
import xyz.tavenservices.spigot.knockback.CraftKnockbackProfile;

import java.util.Collections;

public class KnockbackCommand extends Command {

    public KnockbackCommand(String name) {
        super(name);
        this.setAliases(Collections.singletonList("kb"));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "No permission :(");
            return true;
        }

        if (args.length == 0) {
            CraftKnockbackProfile currentProfile = (CraftKnockbackProfile) KnockbackConfig.getCurrentKb();
            sender.sendMessage(ChatColor.WHITE + "Current knockback values: " +
                    ChatColor.AQUA + currentProfile.getHorizontal() +
                    ChatColor.WHITE + ", " + ChatColor.AQUA +
                    currentProfile.getVertical() +
                    ChatColor.WHITE + ", " + ChatColor.AQUA +
                    currentProfile.getRangeFactor() +
                    ChatColor.WHITE + ", " + ChatColor.AQUA +
                    currentProfile.getMaxRangeReduction() +
                    ChatColor.WHITE + ", " + ChatColor.AQUA +
                    currentProfile.getStartRangeReduction() +
                    ChatColor.WHITE + ", " + ChatColor.AQUA +
                    currentProfile.getMinRange() +
                    ChatColor.WHITE + ", " + ChatColor.AQUA +
                    currentProfile.getFriction());

            sender.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "Knockback Commands:");
            sender.sendMessage(ChatColor.AQUA + "/kb create <profile>" + ChatColor.WHITE + " - Create a new knockback profile.");
            sender.sendMessage(ChatColor.AQUA + "/kb delete <profile>" + ChatColor.WHITE + " - Delete an existing knockback profile.");
            sender.sendMessage(ChatColor.AQUA + "/kb load <profile>" + ChatColor.WHITE + " - Load a knockback profile.");
            sender.sendMessage(ChatColor.AQUA + "/kb set <profile> <modifier> <value>" + ChatColor.WHITE + " - Modify a knockback value.");
            sender.sendMessage(ChatColor.AQUA + "/kb reload" + ChatColor.WHITE + " - Reload the knockback configuration.");
            sender.sendMessage(ChatColor.AQUA + "/kb setprofile <player> <profile>" + ChatColor.WHITE + " - Set a knockback profile for a player.");
        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            FulfillSpigot.getInstance().reloadKnockbackConfig();
            sender.sendMessage(ChatColor.WHITE + "Knockback configuration reloaded.");
        } else if (args.length == 2) {
            String subCommand = args[0];
            String profileName = args[1];

            if (subCommand.equalsIgnoreCase("create")) {
                CraftKnockbackProfile newProfile = new CraftKnockbackProfile(profileName);
                newProfile.save();
                sender.sendMessage(ChatColor.WHITE + "Knockback profile " + ChatColor.AQUA + profileName + ChatColor.WHITE + " created.");
            } else if (subCommand.equalsIgnoreCase("delete")) {
                CraftKnockbackProfile profile = (CraftKnockbackProfile) KnockbackConfig.getKbProfileByName(profileName);
                if (profile != null) {
                    KnockbackConfig.getKbProfiles().remove(profile);
                    sender.sendMessage(ChatColor.WHITE + "Knockback profile " + ChatColor.AQUA + profileName + ChatColor.WHITE + " deleted.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Profile " + profileName + " not found.");
                }
            } else if (subCommand.equalsIgnoreCase("load")) {
                CraftKnockbackProfile profile = (CraftKnockbackProfile) KnockbackConfig.getKbProfileByName(profileName);
                if (profile != null) {
                    KnockbackConfig.setCurrentKb(profile);
                    sender.sendMessage(ChatColor.WHITE + "Loaded knockback profile " + ChatColor.AQUA + profileName + ChatColor.WHITE + ".");
                } else {
                    sender.sendMessage(ChatColor.RED + "Profile " + profileName + " not found.");
                }
            } else {
                sender.sendMessage(this.usageMessage);
            }
        } else if (args.length == 4 && args[0].equalsIgnoreCase("set")) {
            String profileName = args[1];
            String valueType = args[2];
            double value;

            try {
                value = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid value.");
                return true;
            }

            CraftKnockbackProfile profile = (CraftKnockbackProfile) KnockbackConfig.getKbProfileByName(profileName);
            if (profile != null) {
                switch (valueType.toLowerCase()) {
                    case "horizontal":
                        profile.setHorizontal(value);
                        sender.sendMessage(ChatColor.WHITE + "Set horizontal to " + ChatColor.AQUA + value + ChatColor.WHITE + " for profile " + ChatColor.AQUA + profileName + ChatColor.WHITE + ".");
                        break;
                    case "vertical":
                        profile.setVertical(value);
                        sender.sendMessage(ChatColor.WHITE + "Set vertical to " + ChatColor.AQUA + value + ChatColor.WHITE + " for profile " + ChatColor.AQUA + profileName + ChatColor.WHITE + ".");
                        break;
                    case "rangefactor":
                        profile.setRangeFactor(value);
                        sender.sendMessage(ChatColor.WHITE + "Set range factor to " + ChatColor.AQUA + value + ChatColor.WHITE + " for profile " + ChatColor.AQUA + profileName + ChatColor.WHITE + ".");
                        break;
                    case "maxrangereduction":
                        profile.setMaxRangeReduction(value);
                        sender.sendMessage(ChatColor.WHITE + "Set max range reduction to " + ChatColor.AQUA + value + ChatColor.WHITE + " for profile " + ChatColor.AQUA + profileName + ChatColor.WHITE + ".");
                        break;
                    case "startrangereduction":
                        profile.setStartRangeReduction(value);
                        sender.sendMessage(ChatColor.WHITE + "Set start range reduction to " + ChatColor.AQUA + value + ChatColor.WHITE + " for profile " + ChatColor.AQUA + profileName + ChatColor.WHITE + ".");
                        break;
                    case "minrange":
                        profile.setMinRange(value);
                        sender.sendMessage(ChatColor.WHITE + "Set idle range to " + ChatColor.AQUA + value + ChatColor.WHITE + " for profile " + ChatColor.AQUA + profileName + ChatColor.WHITE + ".");
                        break;
                    case "friction":
                        profile.setFriction(value);
                        sender.sendMessage(ChatColor.WHITE + "Set friction to " + ChatColor.AQUA + value + ChatColor.WHITE + " for profile " + ChatColor.AQUA + profileName + ChatColor.WHITE + ".");
                        break;
                    default:
                        sender.sendMessage(ChatColor.RED + "Unknown value." + ChatColor.WHITE + " Available modifiers: " + ChatColor.AQUA + "horizontal, vertical, rangefactor, maxrangereduction, startrange, minrange, friction.");
                }
                profile.save();
            } else {
                sender.sendMessage(ChatColor.RED + "Profile " + profileName + " not found.");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setprofile")) {
            String playerName = args[1];
            String profileName = args[2];

            Player target = Bukkit.getPlayer(playerName);
            if (target != null) {
                CraftKnockbackProfile profile = (CraftKnockbackProfile) KnockbackConfig.getKbProfileByName(profileName);
                if (profile != null) {
                    target.setKnockbackProfile(profile);
                    sender.sendMessage(ChatColor.WHITE + "Set knockback profile " + ChatColor.AQUA + profileName + ChatColor.WHITE + " for player " + ChatColor.AQUA + playerName + ChatColor.WHITE + ".");
                } else {
                    sender.sendMessage(ChatColor.RED + "Profile " + profileName + " not found.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Player " + playerName + " is not online.");
            }
        } else {
            sender.sendMessage(this.usageMessage);
        }

        return true;
    }
}
