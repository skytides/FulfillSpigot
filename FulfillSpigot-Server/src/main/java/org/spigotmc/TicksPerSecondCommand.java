package org.spigotmc;

import net.minecraft.server.MinecraftServer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TicksPerSecondCommand extends Command {

    public TicksPerSecondCommand(String name) {
        super(name);
        this.description = "Gets the current ticks per second for the server";
        this.usageMessage = "/tps";
        this.setPermission("bukkit.command.tps");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }
        // FloretSpigot start
        // Fetch TPS for the last 5 seconds and 5 minutes
        double[] tps = org.bukkit.Bukkit.spigot().getTPS();
        double tps5Seconds = tps[0];
        double tps5Minutes = tps[1];

        // Format TPS values
        String formattedTps5Seconds = format(tps5Seconds);
        String formattedTps5Minutes = format(tps5Minutes);

        String tpsMessage = ChatColor.GOLD + "Current Server TPS: " + ChatColor.GREEN + formattedTps5Seconds + ChatColor.YELLOW + "/" + ChatColor.GREEN + formattedTps5Minutes;
        sender.sendMessage(tpsMessage);

        // MS per Tick
        sender.sendMessage(ChatColor.YELLOW + "MS per Tick: " + ChatColor.GREEN + Math.round(MinecraftServer.getServer().getLastMspt() * 100.0) / 100.0);

        return true;
    }

    private static String format(double tps) {
        return (((tps > 18.0) ? ChatColor.GREEN : (tps > 16.0) ? ChatColor.YELLOW : ChatColor.RED).toString())
            + (((tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0));
    }
}

// end
